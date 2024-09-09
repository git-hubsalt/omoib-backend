package com.githubsalt.omoib.global.config.security;

import com.githubsalt.omoib.global.dto.CustomOAuth2User;
import com.githubsalt.omoib.global.dto.CustomUserInfoDTO;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    @Value("${login.redirect.url}")
    private String loginRedirectUrl;
    private final UserRepository userRepository;

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
        Long userId;
        boolean isNewUser;
        if (optionalUser.isEmpty()) {
            User user = User.builder()
                .socialId(kakaoUserId)
                .build();
            userRepository.save(user);
            userId = user.getId();
            isNewUser = true;
        } else {
            User user = optionalUser.get();
            userId = user.getId();
            isNewUser = user.getName() == null;
        }
        String accessToken = jwtProvider.createAccessToken(new CustomUserInfoDTO(userId));
        getRedirectStrategy().sendRedirect(request, response, getRedirectUrl(loginRedirectUrl, accessToken, isNewUser));
    }

    private String getRedirectUrl(String targetUrl, String token, boolean isNewUser) {
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", token)
            .queryParam("isNewUser", isNewUser)
            .build().toUriString();
    }

}