package ru.kpfu.itis.shakirov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }


    @PostMapping("/add")
    public String addUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        UserDto userDto = userService.createUser(username, password);
        model.addAttribute("user", userDto);
        return "user";
    }

    @GetMapping("/edit/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @RequestParam("username") String username, Model model) {
        boolean bool = userService.updateUser(id, username);
        model.addAttribute("bool", bool);
        return "bool";
    }

    @GetMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}