package com.naegwon.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.auth.LoginUser;
import com.naegwon.bank.dto.user.UserReqDto;
import com.naegwon.bank.dto.user.UserReqDto.LoginReqDto;
import com.naegwon.bank.dto.user.UserRespDto;
import com.naegwon.bank.dto.user.UserRespDto.LoginRespDto;
import com.naegwon.bank.util.CustomResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");// /api/login 으로 바꿈
        this.authenticationManager = authenticationManager;
    }

    //Post: 원래 /login 으로 오면 동작함
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그: attemptAuthentication 호출됨");

        try {
            ObjectMapper om = new ObjectMapper();
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            //강제 로그인
            //토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword()
            );

            //UserDetailsService의 loadUserByUserName 호출
            //JWT를 쓴다 하더라도, 컨트롤러 진입을 하면 Security 권한 체크, 인증 체크의 도움을 받을 수 있게 세션을 만든다.
            //이 세션의 유효기간은 request하고, response하면 끝
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // unsuccessfulAuthentication 호출됨
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    //로그인 실패시 호출됨
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                              AuthenticationException failed) throws IOException, ServletException {
        log.debug("디버그: unsuccessfulAuthentication 호출");
        log.error(failed.getMessage());
        CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);
    }

    //return authenticate 가 잘 작동하면 successfulAuthentication 가 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug("디버그: successfulAuthentication 호출됨");

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginRespDto);
    }
}
