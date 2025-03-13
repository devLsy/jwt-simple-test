package com.test.lsy.jwt1.auth;

import com.test.lsy.jwt1.model.User;
import com.test.lsy.jwt1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:9090/login 시 요청 됨
@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = repository.findByUsername(username);
        log.info("userEntity::: , {}", userEntity);
        return new PrincipalDetails(userEntity);
    }
}
