package ru.kpfu.itis.shakirov.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.model.Role;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.UserRepository;
import ru.kpfu.itis.shakirov.config.properties.MailProperties;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JavaMailSender mailSender,
                       MailProperties mailProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).toList();
    }

    @Transactional
    public UserDto getUserById(Long id) {
        return UserDto.fromEntity(userRepository.findById(id).orElse(null));
    }

    @Transactional
    public UserDto createUser(String username, String email, String password) {

        String verificationCode = UUID.randomUUID().toString();

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1, "USER"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerificationCode(verificationCode);
        user.setRoles(roles);
        user.setVerified(false);

        User savedUser = userRepository.save(user);

        sendVerificationMail(username, email, verificationCode);

        return UserDto.fromEntity(savedUser);
    }

    private void sendVerificationMail(String username, String email, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        String content = mailProperties.content();
        try {
            helper.setFrom(mailProperties.from(), mailProperties.sender());
            helper.setTo(email);
            helper.setSubject(mailProperties.subject());

            content = content.replace("${name}", username);
            content = content.replace("${url}", "\"" + mailProperties.baseUrl() + "/verification?code=" + verificationCode + "\"");

            helper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Transactional
    public boolean updateUser(Long id, String username) {
        int count = userRepository.updateUsernameById(username, id);
        return count > 0;
    }

    @Transactional
    public boolean verifyUser(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));
        if (user.isVerified()) {
            return false;
        }
        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public Optional<UserDto> getUserByUsernameAndPassword(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(UserDto::fromEntity);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}