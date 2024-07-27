package com.naegwon.bank.service;

import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserEnum;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.user.UserReqDto;
import com.naegwon.bank.dto.user.UserRespDto;
import com.naegwon.bank.dto.user.UserRespDto.JoinRespDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.naegwon.bank.dto.user.UserReqDto.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//가짜 환경이기에 Spring 관리 Bean들이 없는 상태
@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    @Mock //가짜 객체
    private UserRepository userRepository;
    
    @Spy //진짜 IoC 객체를 꺼내는 것
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_test() throws Exception{
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("test");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("test@test.com");
        joinReqDto.setFullname("테스트");

        //stub
        //JPA는 굳이 테스트 필요 없음
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        User user = newMockUser(1L, "test", "테스트");
        when(userRepository.save(any())).thenReturn(user);

        //when
        JoinRespDto joinRespDto = userService.join(joinReqDto);
        System.out.println("joinRespDto = " + joinRespDto.toString());

        //then
        assertEquals(joinRespDto.getId(), 1L);
        assertEquals(joinRespDto.getUsername(), "test");
    }

}