package com.naegwon.bank.service;

import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserEnum;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.user.UserReqDto;
import com.naegwon.bank.dto.user.UserRespDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.naegwon.bank.dto.user.UserReqDto.*;
import static com.naegwon.bank.dto.user.UserRespDto.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BCryptPasswordEncoder passwordEncoder;

    //서비스는 DTO로 요청받고 DTO로 응답한다.
    @Transactional //트랜잭션이 메서드 시작할때, 시작되고, 종료될 때 함께 종료
    public JoinRespDto join(JoinReqDto joinReqDto) {
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOptional = userRepository.findByUsername(joinReqDto.getUsername());
        if (userOptional.isPresent()) {
            //Username 중복
            throw new CustomApiException("동일한 Username이 존재합니다.");
        }

        // 2. 패스워드 인코딩 - 회원가입
        User userPersistence = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return new JoinRespDto(userPersistence);
    }




}
