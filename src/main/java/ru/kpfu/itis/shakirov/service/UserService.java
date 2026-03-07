package ru.kpfu.itis.shakirov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.UserRepository;
import ru.kpfu.itis.shakirov.repository.UserRepositoryHibernate;

import java.util.List;

@Service
public class UserService {

    private final UserRepositoryHibernate userRepositoryHibernate;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepositoryHibernate userRepositoryHibernate, UserRepository userRepository) {
        this.userRepositoryHibernate = userRepositoryHibernate;
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(a -> UserDto.fromEntity(a)).toList();
    }

    public UserDto getUserById(Long id) {
        return UserDto.fromEntity(userRepository.findById(id).orElse(null));
    }

    public UserDto createUser(String username) {
        return UserDto.fromEntity(userRepository.save(new User(null, username)));
    }

    public boolean updateUser(Long id, String username) {
        int count = userRepository.updateUsernameById(username, id);
        return count > 0;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
