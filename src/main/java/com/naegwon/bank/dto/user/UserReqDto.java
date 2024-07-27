package com.naegwon.bank.dto.user;

import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserReqDto {
    @Getter
    @Setter
    public static class JoinReqDto {

        @NotEmpty // null or 공백일 수 없다
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        private String email;

        @NotEmpty
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
