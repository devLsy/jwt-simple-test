package com.test.lsy.jwt1.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("필터3333");

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        // 토큰: dev 이걸 만들어 줘야 함
        // id, pw 정상적으로 들어와서 로그인이 완료되면 토큰을 만들어주고 그걸 응답을 해준다.
        // 요청할 때마다 header의 Authorization에 value값으로 토큰을 가지고 온다.
        // 그 때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지 검증만 하면 됨(RSA, HS256 방식)
        if(req.getMethod().equals("POST")) {
            log.info("POST Requested~");
            String headerAuth = req.getHeader("Authorization");

            log.info("필터111111");
            log.info("headerAuth: {}", headerAuth);

            if(headerAuth.equals("DEV")) {
                filterChain.doFilter(req, res);
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증안됨");
            }
        }
    }
}
