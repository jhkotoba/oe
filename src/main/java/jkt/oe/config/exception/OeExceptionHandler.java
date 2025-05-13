package jkt.oe.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;

/**
 * 전역 예외 처리기 클래스
 */
@Component
@Order(-2)
public class OeExceptionHandler implements WebExceptionHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(OeExceptionHandler.class);
	
	/**
     * 예외 처리 메서드
     * @param exchange ServerWebExchange 객체
     * @param ex 발생한 Throwable 예외
     * @return Mono<Void>
     */
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

		// 로그 출력
		if (ex instanceof SystemException se) {			
			logger.error(se.getReason().getMessage(), se);
		}else {
			logger.error(ex.getMessage(), ex.getCause());
		}		

	    // HTTP 500 상태코드 설정
	    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
	    // 빈 바디로 응답 완료
	    return exchange.getResponse().setComplete();
	}

}
