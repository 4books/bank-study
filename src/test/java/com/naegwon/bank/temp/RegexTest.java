package com.naegwon.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

//java.util.regex.Pattern
public class RegexTest {

    @Test
    public void 한글만된다() throws Exception{
        String value = "한글";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("result = " + result);
    }

    @Test
    public void 한글안된다() throws Exception{
        String value = "한글";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        System.out.println("result = " + result);
    }

    @Test
    public void 영어만된다() throws Exception{
        String value = "english";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("result = " + result);
    }

    @Test
    public void 영어는안된다() throws Exception{
        String value = "한글";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        System.out.println("result = " + result);
    }

    @Test
    public void 영어와숫자만된다() throws Exception{
        String value = "english123";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("result = " + result);
    }

    @Test
    public void 영어만되고_길이는최소2최대4이다() throws Exception{
        String value = "qwer";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("result = " + result);
    }

    //username, email, fullname 테스트
    @Test
    public void user_username_test() throws Exception{
        String username = "test";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("result = " + result);
    }

    @Test
    public void user_fullname_test() throws Exception{
        String username = "test테스트1";
        //영어 한글 숫자 2~20자
        boolean result = Pattern.matches("^[a-zA-Z가-힣0-9]{2,20}$", username);
        System.out.println("result = " + result);
    }

    @Test
    public void user_email_test() throws Exception{
        String username = "test@test.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,}@[a-zA-Z0-9]{2,20}\\.[a-zA-Z]{2,3}$", username);
        System.out.println("result = " + result);
    }

}
