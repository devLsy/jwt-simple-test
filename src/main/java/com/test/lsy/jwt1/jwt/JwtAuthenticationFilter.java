package com.test.lsy.jwt1.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
// http://localhost:9090/login 요청이오면 UsernamePasswordAuthenticationFilter가 가로채어 
// attemptAuthentication() 함수를 실행
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    
    // login 요청 시 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        
        // 1. username, password를 받음
        // 2. 정상인지 로그인 시도(authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출되고
        //    그 서비스의 loadUserByUSername()이 호출되고 거기서 인증처리함
        // 3. PrincipalDetails객체를 세션에 담고(시큐리티의 권한관리를 위해 세션에 담음) JWT 토큰 생성 후 응답하면 됨 
        
        return super.attemptAuthentication(request, response);
    }
}
