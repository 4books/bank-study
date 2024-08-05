package com.naegwon.bank.config.jwt;

import com.naegwon.bank.config.auth.LoginUser;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProcessTest {

    private String createToken() {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginuser = new LoginUser(user);

        //when
        String jwtToken = JwtProcess.create(loginuser);
        System.out.println("jwtToken = " + jwtToken);

        return jwtToken;
    }

    @Test
    public void create_test() throws Exception{
        //given
        String jwtToken = createToken();

        //then
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }

    @Test
    public void verify_test() throws Exception{
        //given
        String jwtToken = createToken().replace(JwtVO.TOKEN_PREFIX, "");

        //when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser = " + loginUser.getUser().getId());
        System.out.println("loginUser = " + loginUser.getUser().getRole());

        //then
        assertEquals(1L, (long) loginUser.getUser().getId());
        assertEquals(UserEnum.CUSTOMER, loginUser.getUser().getRole());
    }
}