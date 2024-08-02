package com.naegwon.bank.config.jwt;

import com.naegwon.bank.config.auth.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * 모든 주소에서 동작함 (토큰 검증)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (isHeaderVerify(request, response)) {
            //토큰 존재(검증은 아직 아님)
            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            LoginUser loginUser = JwtProcess.verify(token);

            //임시 세션
            //password는 모르니 null
            //(UserDetails 타입 or Username)
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);

    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(JwtVO.HEADER);
        if(header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)){
            return false;
        }
        return true;
    }
}
