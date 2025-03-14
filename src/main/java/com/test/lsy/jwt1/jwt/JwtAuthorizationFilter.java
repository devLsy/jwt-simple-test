package com.test.lsy.jwt1.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.test.lsy.jwt1.auth.PrincipalDetails;
import com.test.lsy.jwt1.model.User;
import com.test.lsy.jwt1.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// 시큐리티가 가지고 있는 filter 중 BasicAuthenticationFilter라는게 있고
// 권한, 인증이 필요한 특정 주소를 요청 했을 때 위 필터를 무조건 타게 되어 있음
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository repository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository repository) {
        super(authenticationManager);
        this.repository = repository;
    }
    
    // 인증이나 권한이 필요한 주소요청이 있을 때 이 필터를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        log.info("jwtHeader :: {}", jwtHeader);

        // header가 없거나 Bearer로 시작하지 않으면 필터 태우고 return
        if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace("Bearer ", "");

        log.info("parsed jwtToken :: {}", jwtToken);

        // JWT 토큰 서명
        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                            .verify(jwtToken)
                            .getClaim("username")
                            .asString();

        // 정상적으로 서명되었다는 의미(올바른 사용자)
        if(username != null) {
            log.info("서명이 정상적으로 되었음, 올바른 사용자임");
            User findUser = repository.findByUsername(username);
            // 조회된 유저 엔티티를 PrincipalDetails에 담음
            PrincipalDetails principalDetails = new PrincipalDetails(findUser);
            // JWT 토큰 서명을 통해 정상이면 authentication 객체 생성 
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            // 시큐리티 세션에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            chain.doFilter(request, response);
        }
        chain.doFilter(request, response);
    }
}
