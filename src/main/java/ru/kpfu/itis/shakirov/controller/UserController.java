package ru.kpfu.itis.shakirov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.shakirov.aop.annotation.Benchmark;
import ru.kpfu.itis.shakirov.aop.annotation.Metric;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Metric
    @Benchmark
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @Metric
    @Benchmark
    @PostMapping("/add")
    public String addUser(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, Model model) {

        UserDto userDto = userService.createUser(username, email, password);
        model.addAttribute("user", userDto);
        return "login";
    }

    @Metric
    @Benchmark
    @GetMapping("/edit/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @Metric
    @Benchmark
    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @RequestParam("username") String username, Model model) {
        boolean bool = userService.updateUser(id, username);
        model.addAttribute("bool", bool);
        return "bool";
    }

    @Metric
    @Benchmark
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}