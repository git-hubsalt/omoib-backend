package com.githubsalt.omoib.global.config.security;

import com.githubsalt.omoib.aws.s3.PresignedURLBuilder;
import com.githubsalt.omoib.global.dto.CustomOAuth2User;
import com.githubsalt.omoib.global.dto.CustomUserInfoDTO;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    @Value("${login.redirect.url}")
    private String loginRedirectUrl;
    private final UserRepository userRepository;
    private final PresignedURLBuilder presignedURLBuilder;

    /**
     * isNewUser가 true인 경우 : 처음으로 로그인하거나 로그인은 했었지만 회원 가입 완료를 하지 않은 유저
     * url 파라미터 : jwt, isNewUser (true이면 회원가입 페이지로 이동, false이면 메인 페이지로 이동)
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String kakaoUserId = oAuth2User.getAttributes().get("id").toString();
        Optional<User> optionalUser = userRepository.findBySocialId(kakaoUserId);
        boolean isNewUser;
        User user;
        if (optionalUser.isEmpty()) {
            User userBuilder = User.builder()
                .socialId(kakaoUserId)
                .email(oAuth2User.getEmail())
                .build();
            user = userRepository.save(userBuilder);
            isNewUser = true;
        } else {
            user = optionalUser.get();
            updateEmail(user, oAuth2User.getEmail());
            isNewUser = user.getName() == null;
        }
        String accessToken = jwtProvider.createAccessToken(new CustomUserInfoDTO(user.getId()));
        String redirectUrl = getRedirectUrl(loginRedirectUrl, accessToken, isNewUser, user);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getRedirectUrl(String targetUrl, String token, boolean isNewUser, User user) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam("isNewUser", isNewUser);
        if (user.getName() != null) {
            String encodingName = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);
            builder.queryParam("username", encodingName);
        }
        if (user.getProfileImagePath() != null) {
            builder.queryParam("profileUrl", presignedURLBuilder.buildGetPresignedURL(user.getProfileImagePath()));
        }
        return builder.build().toUriString();
    }

    private void updateEmail(User user, String email) {
        if (user.getEmail() == null) {
            user.setEmail(email);
        }
    }

}