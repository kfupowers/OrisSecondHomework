package ru.kpfu.itis.shakirov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.model.Role;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService( UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(a -> UserDto.fromEntity(a)).toList();
    }

    @Transactional
    public UserDto getUserById(Long id) {
        return UserDto.fromEntity(userRepository.findById(id).orElse(null));
    }

    @Transactional
    public UserDto createUser(String username, String password) {
        ArrayList<Role> roles =  new ArrayList<Role>();
        roles.add(new Role(1, "USER"));
        return UserDto.fromEntity(userRepository.save(new User(null, username, passwordEncoder.encode(password), roles)));
    }

    @Transactional
    public boolean updateUser(Long id, String username) {
        int count = userRepository.updateUsernameById(username, id);
        return count > 0;
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
