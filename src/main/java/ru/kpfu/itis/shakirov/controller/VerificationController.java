package ru.kpfu.itis.shakirov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.shakirov.aop.annotation.Benchmark;
import ru.kpfu.itis.shakirov.aop.annotation.Metric;
import ru.kpfu.itis.shakirov.service.UserService;

@Controller
public class VerificationController {

    private final UserService userService;

    @Autowired
    public VerificationController(UserService userService) {
        this.userService = userService;
    }

    @Metric
    @Benchmark
    @GetMapping("/verification")
    public String verify(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        try {
            boolean verified = userService.verifyUser(code);
            if (verified) {
                redirectAttributes.addFlashAttribute("message", "Email успешно подтверждён! Теперь вы можете войти.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Неверный или просроченный код подтверждения.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Произошла ошибка при подтверждении. Попробуйте позже.");
        }
        return "redirect:/login";
    }
}