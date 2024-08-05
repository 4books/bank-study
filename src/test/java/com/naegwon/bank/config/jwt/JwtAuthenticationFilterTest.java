package com.naegwon.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.user.UserReqDto.LoginReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//SpringBootTest 하는 곳에는 전부 다 teardown.sql을 붙여주자
//모든 데이터를 Truncate해서 다른 테스트에 영향을 주지 않게 하기 위해서
//@Transactional
@Sql("classpath:db/teardown.sql") //실행 시점: BeforeEach 실행 직전에 실행
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception{
        userRepository.save(newUser("test", "테스트"));
    }

    @Test
    public void successfulAuthentication_test() throws Exception{
        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("test");
        loginReqDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("responseBody = " + responseBody);
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        //최상단 -> data 필드 -> username
        resultActions.andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    public void unsuccessfulAuthentication_test() throws Exception {
        //given
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername("test");
        loginReqDto.setPassword("12345"); //비밀번호 틀림
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("responseBody = " + responseBody);
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }



}