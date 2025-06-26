package jkt.oe.config.security;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${gateway.secret.key}")
	private String gsKey;
	
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
        
        // GS에서 전달한 키
        String gsKey = request.getHeaders().getFirst("X-Gateway-Secret");

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
        
        if (this.gsKey.equals(gsKey) == false) {
        	exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        	exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        	return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
}
