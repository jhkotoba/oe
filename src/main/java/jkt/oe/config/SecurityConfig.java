//package jkt.oe.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//	@Bean
//    protected SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//            // CSRF를 비활성화하고 싶다면
//            .csrf(csrf -> csrf.disable())
//            
//            // 리액티브 방식의 HTTP Basic 인증 활성화 (선택)
//            .httpBasic(Customizer.withDefaults())
//            
//            // 폼 로그인 활성화
//            .formLogin(Customizer.withDefaults())
//            
//            // 라우트별 접근 권한 설정
//            .authorizeExchange(exchanges -> exchanges
//                .pathMatchers("/login/**").permitAll()  // 로그인
//                .pathMatchers("/signup/**").permitAll() // 회원가입
//                .anyExchange().authenticated() // 나머지는 인증 필요
//            )
//            
//            // 세션/Context 저장소 설정
//            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
//            
//            .build();
//    }
//}
