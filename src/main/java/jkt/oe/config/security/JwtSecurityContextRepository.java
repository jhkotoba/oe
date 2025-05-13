package jkt.oe.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository{

	private final JwtAuthenticationManager authenticationManager;
	
//	public JwtSecurityContextRepository(JwtAuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
	
	// save() 메서드: stateless 애플리케이션이므로 저장 기능은 구현하지 않습니다.
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
         return Mono.empty();
    }

    // load() 메서드: HTTP 요청으로부터 SecurityContext를 생성합니다.
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
         // 1. 요청 헤더에서 Authorization 값을 가져옵니다.
         String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

         // 2. 헤더가 존재하고 "Bearer "로 시작하는지 확인합니다.
         if (authHeader != null && authHeader.startsWith("Bearer ")) {
             // 3. "Bearer " 부분을 제거하고 순수 JWT 토큰 문자열을 추출합니다.
             String authToken = authHeader.substring(7);

             // 4. 추출한 토큰을 기반으로 Authentication 객체를 생성합니다.
             // 여기서 principal과 credentials 모두 토큰 자체를 넣어둡니다.             			               
             Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

             // 5. authenticationManager에 인증 처리를 위임하여, 성공 시 SecurityContextImpl 객체를 생성합니다.
             return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
         }
         // 6. 유효한 Authorization 헤더가 없으면 빈 Mono를 반환합니다.
         return Mono.empty();
    }
	
    
    
	
}
