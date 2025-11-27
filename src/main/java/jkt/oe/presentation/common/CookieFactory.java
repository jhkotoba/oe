package jkt.oe.presentation.common;

import java.time.Duration;

import org.springframework.stereotype.Component;

import jkt.oe.config.constant.CookieConst;
import jkt.oe.config.constant.OeConst;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

@Component
public class CookieFactory {

    // JWT Access Token 만료 시간 (초)
    @Value("${custom.jwt.access-expiration}")
    private Long accessExpiration;

    // JWT Refresh Token 만료 시간 (초)
    @Value("${custom.jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * 쿠키 생성 
     * @param name - 쿠키 이름
     * @param value - 쿠키 값
     * @param maxAge - 쿠키 유효 기간
     * @return ResponseCookie
     */
    public Mono<ResponseCookie> createCookie(String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite(CookieConst.STRICT)
                .build();
        return Mono.just(cookie);
    }

    /**
     * AccessToken 쿠키 생성
     * @param token AccessToken 값
     * @return ResponseCookie
     */
    public Mono<ResponseCookie> createAccessTokenCookie(String token) {
        return Mono.just(
                ResponseCookie.from(OeConst.ACCESS_TOKEN, token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite(CookieConst.STRICT)
                        .path("/")
                        .maxAge(Duration.ofSeconds(this.accessExpiration))
                        .build());
    }

    /**
     * RefreshToken 쿠키 생성
     * @param token RefreshToken 값
     * @return ResponseCookie
     */
    public Mono<ResponseCookie> createRefreshTokenCookie(String token) {
        return Mono.just(
                ResponseCookie.from(OeConst.REFRESH_TOKEN, token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite(CookieConst.STRICT)
                        .path("/")
                        .maxAge(Duration.ofSeconds(this.refreshExpiration))
                        .build());
    }

}
