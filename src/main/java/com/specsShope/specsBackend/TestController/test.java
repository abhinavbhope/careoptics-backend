package com.specsShope.specsBackend.TestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {
    @GetMapping("/healthz")
    public String healthCheck() {
        return "OK";
    }
}
