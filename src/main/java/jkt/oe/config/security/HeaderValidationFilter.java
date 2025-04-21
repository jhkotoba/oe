package jkt.oe.config.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import jkt.oe.config.constant.HeaderConst;
import jkt.oe.config.util.RequestUtil;
import reactor.core.publisher.Mono;

/**
 * 요청한 클라이언트의 IP 주소와 User-Agent 헤더를 검사하여 유효하지 않을 경우 요청을 차단하는 필터
 */
public class HeaderValidationFilter implements WebFilter {

	/**
	 * 요청 필터 체인을 실행하기 전에 클라이언트 정보를 검증
	 */
	@Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 클라이언트 IP 주소 추출
        String ip = RequestUtil.getClientIp(request);
        
        // User-Agent 헤더 추출
        String userAgent = request.getHeaders().getFirst(HeaderConst.USER_AGENT);

        // IP가 없는 경우 요청 차단 (400 Bad Request)
        if (ip == null || ip.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().setComplete();
        }
        
        // User-Agent가 없는 경우 요청 차단
        if (userAgent == null || userAgent.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().setComplete();
        }
        
        // 로컬 IP만 접근 체크
        if(!"127.0.0.1".equals(ip) && !"localhost".equals(ip)) {
        	exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        	exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
}
