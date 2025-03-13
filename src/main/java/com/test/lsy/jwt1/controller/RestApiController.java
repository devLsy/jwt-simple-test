package com.test.lsy.jwt1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestApiController {

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }
}
