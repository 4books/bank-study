package com.naegwon.bank.domain.config;

import com.naegwon.bank.domain.user.UserEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그: BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    //TODO JWT 필터 등록이 필요함

    // JWT 서버 생성 예정. Session 미사용
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        log.debug("디버그: filterChain 빈 등록됨");

        // iframe 미사용
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http.cors(cors -> cors.configurationSource(configurationSource()));

        // JSessionId를 서버에서 관리하지 않음
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 폼 로그인 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/s/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole(UserEnum.ADMIN.name())
                .anyRequest().permitAll()
        );

        return http.build();
    }

    public CorsConfigurationSource configurationSource(){
        log.debug("디버그: CorsConfigurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); //GET POST PUT DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); //모든 IP 주소 허용 (프론트 엔드 IP만 허용 react)
        configuration.setAllowCredentials(true); //클라이언트에서 쿠키 요청 허용

        //모든 주소에 위 설정을 넣어준다
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
