package ru.kpfu.itis.shakirov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kpfu.itis.shakirov.aop.annotation.Benchmark;
import ru.kpfu.itis.shakirov.aop.annotation.Metric;

@Controller
public class LoginController {

    @Metric
    @Benchmark
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}