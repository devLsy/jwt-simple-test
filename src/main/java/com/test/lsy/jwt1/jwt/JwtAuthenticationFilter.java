package com.test.lsy.jwt1.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.lsy.jwt1.auth.PrincipalDetails;
import com.test.lsy.jwt1.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Date;

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
            // authentication값이 있으면 세션에 저장되었고 즉, 로그인이 되었다는 의미
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("로그인 성공~~~");
            log.info(principalDetails.getUser().getUsername());
            log.info("=================================================");
            // 리턴의 이유는 시큐리티가 권한처리를 해주기 때문에 편하라고 한 것임
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // attemptAuthentication() 실행 후 인증이 정상적으로 되면 실행되는 함수
    // 여기서 JWT 토큰 생성 후 request한 사용자에게 response해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        log.info("진짜 인증되었다면 찍힐 것임~~~~");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
            
        // JWT 토큰 생성(Hash암호방식)
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                // 만료시간
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*10)))
                // 내가 넣고 싶은 클레임값
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                // 암호화 키
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));
        // 응답 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer "+jwtToken);
    }
}
