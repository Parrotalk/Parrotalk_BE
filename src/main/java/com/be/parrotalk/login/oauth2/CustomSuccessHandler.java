package com.be.parrotalk.login.oauth2;

import com.be.parrotalk.login.dto.TokenResponseDto;
import com.be.parrotalk.login.service.AuthService;
import com.be.parrotalk.login.service.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final RedisService redisService;

    @Value("${ngrok.url}")
    private String ngrokUrl;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        TokenResponseDto tokenResponse = authService.createJwtTokens(authentication);

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(tokenResponse.getUserInfo().getId().toString(), tokenResponse.getRefreshToken(), Duration.ofDays(7));

        // RefreshToken을 쿠키에 저장
//        response.addCookie(createCookie("refresh", tokenResponse.getRefreshToken(), (int) refreshTokenExpirationTime));

//        // Access Token을 응답 헤더에 추가
//        response.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());

        // 클라이언트가 적절한 경로로 리다이렉트
        String redirectUrl = ngrokUrl + "/call/home?userId=" + tokenResponse.getUserInfo().getId().toString();

        log.info("로그인 성공, 리다이렉트 URL: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

//    private Cookie createCookie(String key, String value, int duration) {
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(duration);
//        cookie.setSecure(true);
//        cookie.setDomain("b0b1-14-138-221-58.ngrok-free.app"); // 상위 도메인으로 설정
//        cookie.setPath("/"); // 모든 경로에서 접근 가능
//        cookie.setHttpOnly(true);
//        log.info("cookie 생성"+cookie);
//
//        return cookie;
//    }

    private boolean isSecureEnvironment() {
        // 개발/운영 환경 분리
        return "production".equals(System.getenv("ENV"));
    }
}