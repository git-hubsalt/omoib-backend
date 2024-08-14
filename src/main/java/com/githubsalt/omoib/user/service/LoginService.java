package com.githubsalt.omoib.user.service;

import com.githubsalt.omoib.global.config.security.JwtProvider;
import com.githubsalt.omoib.global.dto.CustomUserInfoDTO;
import com.githubsalt.omoib.global.dto.KakaoTokenResponseDTO;
import com.githubsalt.omoib.user.domain.User;
import com.githubsalt.omoib.user.dto.LoginResponseDTO;
import com.githubsalt.omoib.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    private final JwtProvider jwtProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-name}")
    private String clientName;

    private static final String ISS = "https://kauth.kakao.com";

    @Transactional
    public LoginResponseDTO login(String code) {
        ClientRegistration provider = inMemoryClientRegistrationRepository.findByRegistrationId(clientName);
        KakaoTokenResponseDTO tokenResponse = getToken(code, provider);
        CustomUserInfoDTO userInfo = getUserInfo(tokenResponse);

        String accessToken = jwtProvider.createAccessToken(userInfo);

        return LoginResponseDTO.builder()
            .token(accessToken)
            .userId(userInfo.getUserId())
            .build();
    }

    private KakaoTokenResponseDTO getToken(String code, ClientRegistration provider) {
        return WebClient.create()
            .post()
            .uri(provider.getProviderDetails().getTokenUri())
            .headers(header -> {
                header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            })
            .bodyValue(tokenRequest(code, provider))
            .retrieve()
            .bodyToMono(KakaoTokenResponseDTO.class)
            .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_secret", provider.getClientSecret());
        formData.add("client_id", provider.getClientId());
        return formData;
    }

    private String getKakaoUserId(String token, String iss, String aud) {
        return (String) getUnsignedTokenClaims(token, iss, aud).getBody().get("sub");
    }

    //카카오 ID 토큰 페이로드 검증
    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {
        return Jwts.parserBuilder()
            .requireAudience(aud)
            .requireIssuer(iss)
            .build()
            .parseClaimsJwt(getUnsignedToken(token));
    }

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    private CustomUserInfoDTO getUserInfo(KakaoTokenResponseDTO tokenResponse) {
        String kakaoUserId = getKakaoUserId(tokenResponse.idToken, ISS, clientId);
//        User user = userRepository.findBySocialId(kakaoUserId).orElseGet(null);
        User user = null;
        if (user == null) {
            user = User.builder()
                .socialId(kakaoUserId)
                .build();
//            userRepository.save(user);
        }
        return new CustomUserInfoDTO(user.getId());
    }

}
