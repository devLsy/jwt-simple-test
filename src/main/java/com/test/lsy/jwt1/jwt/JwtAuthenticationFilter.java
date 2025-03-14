package com.test.lsy.jwt1.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.lsy.jwt1.auth.PrincipalDetails;
import com.test.lsy.jwt1.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
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

        log.info("JwtAuthenticationFilter : 진입");

        try {
//            BufferedReader br = request.getReader();
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                log.info("input :: {}", input);
//            }
            // JSON을 User 엔티티 구조로 파싱
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);

            // 로그인 하기 위해 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            log.info("authenticationToken :: {}", authenticationToken);

            // 토큰을 전달하며 로그인 시도 PrincipalDetailsService의 loadUserByUsername() 함수가 실행되고
            // 정상 로그인되었으면 authentication에 값을 반환
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            
            // authentication에 담긴 값 출력
            // 값이 있으면 authentication 객체가 session영역에 저장됨 ==> 로그인이 되었다는 의미
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("로그인 성공~~~");
            log.info(principalDetails.getUser().getUsername());
            log.info("=================================================");
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
