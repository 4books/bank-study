package com.naegwon.bank.config.auth;

import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Security 로 로그인될 때, Security가 loadUserByUsername 실행해서 username을 체크
    //없으면 오류
    //있으면 정상적으로 Security Context 내부 Session에 로그인된 Session이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );
        return new LoginUser(userPS);
    }
}
