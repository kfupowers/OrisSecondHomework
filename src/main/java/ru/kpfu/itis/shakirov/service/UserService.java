package ru.kpfu.itis.shakirov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(a -> UserDto.fromEntity(a)).toList();
    }
}
