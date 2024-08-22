package com.naegwon.bank.temp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class LongTest {
    
    @Test
    public void long_test() throws Exception{
        //given
        Long number1 = 1111L;
        Long number2 = 1111L;

        //when
        if (number1.longValue() == number2.longValue()) {
            System.out.println("동일합니다.");
        } else {
            System.out.println("동일하지 않습니다..");
        }
        
        Long amount1 = 100L;
        Long amount2 = 1000L;
        if (amount1 < amount2) {
            System.out.println("amount1이 작습니다.");
        } else {
            System.out.println("amount1이 큽니다.");
        }
        //then
        
    }

    @Test
    public void long_test2() throws Exception{
        //given (2의 8승 - 256범위 (-128L ~ 127L)까지는 비교가 됨)
        Long v1 = 127L;
        Long v2 = 127L;

        //when
        if (v1 == v2) {
            System.out.println("테스트 = " + "같습니다");
        }

        //then
    }

    @Test
    public void long_test3() throws Exception{
        //given
        Long v1 = 128L;
        Long v2 = 128L;

        //when

        //then
        assertEquals(v1, v2);

    }


    
}
