package ru.kpfu.itis.shakirov.service;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kpfu.itis.shakirov.config.properties.MailProperties;
import ru.kpfu.itis.shakirov.dto.UserDto;
import ru.kpfu.itis.shakirov.model.Role;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailProperties mailProperties;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setVerified(false);
        testUser.setVerificationCode("abc123");
        testUser.setRoles(List.of(new Role(1, "USER")));

        testUserDto = UserDto.fromEntity(testUser);
    }

    @Test
    void getAllUsers_shouldReturnListOfUserDtos() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturnNull() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserDto result = userService.getUserById(99L);

        assertThat(result).isNull();
        verify(userRepository).findById(99L);
    }

    @Test
    void createUser_shouldSaveUserAndSendVerificationEmail() throws Exception {
        String rawPassword = "secret";
        String encodedPassword = "encodedSecret";
        String verificationCode = UUID.randomUUID().toString();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(mailProperties.content()).thenReturn("Dear ${name}, verify: ${url}");
        when(mailProperties.from()).thenReturn("noreply@test.com");
        when(mailProperties.sender()).thenReturn("Test App");
        when(mailProperties.subject()).thenReturn("Verify your account");
        when(mailProperties.baseUrl()).thenReturn("http://localhost:8080");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        UserDto result = userService.createUser("john_doe", "john@example.com", rawPassword);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
        verify(mailSender).send(mimeMessage);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getVerificationCode()).isNotNull();
        assertThat(savedUser.isVerified()).isFalse();
        assertThat(savedUser.getRoles()).hasSize(1);
    }

    @Test
    void createUser_whenEmailFails_shouldThrowRuntimeException() throws Exception {
        String rawPassword = "secret";
        when(passwordEncoder.encode(rawPassword)).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        when(mailProperties.from()).thenReturn("noreply@test.com");
        when(mailProperties.sender()).thenReturn("Test App");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        doThrow(new MessagingException("SMTP error")).when(mimeMessage).setFrom(any(Address.class));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertThatThrownBy(() -> userService.createUser("john", "john@test.com", rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send verification email");

        verify(userRepository).save(any(User.class));
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void updateUser_whenUserExists_shouldReturnTrue() {
        when(userRepository.updateUsernameById("newUsername", 1L)).thenReturn(1);

        boolean result = userService.updateUser(1L, "newUsername");

        assertThat(result).isTrue();
        verify(userRepository).updateUsernameById("newUsername", 1L);
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldReturnFalse() {
        when(userRepository.updateUsernameById(anyString(), anyLong())).thenReturn(0);

        boolean result = userService.updateUser(99L, "newUsername");

        assertThat(result).isFalse();
        verify(userRepository).updateUsernameById("newUsername", 99L);
    }

    @Test
    void verifyUser_withValidCode_shouldMarkVerifiedAndReturnTrue() {
        when(userRepository.findByVerificationCode("validCode")).thenReturn(Optional.of(testUser));

        boolean result = userService.verifyUser("validCode");

        assertThat(result).isTrue();
        assertThat(testUser.isVerified()).isTrue();
        assertThat(testUser.getVerificationCode()).isNull();
        verify(userRepository).save(testUser);
    }

    @Test
    void verifyUser_withInvalidCode_shouldThrowException() {
        when(userRepository.findByVerificationCode("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.verifyUser("invalid"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid verification code");

        verify(userRepository, never()).save(any());
    }

    @Test
    void verifyUser_withAlreadyVerifiedUser_shouldReturnFalse() {
        testUser.setVerified(true);
        when(userRepository.findByVerificationCode("alreadyVerified")).thenReturn(Optional.of(testUser));

        boolean result = userService.verifyUser("alreadyVerified");

        assertThat(result).isFalse();
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByUsernameAndPassword_withValidCredentials_shouldReturnUserDto() {
        String rawPassword = "secret";
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);

        Optional<UserDto> result = userService.getUserByUsernameAndPassword("john_doe", rawPassword);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john_doe");
    }

    @Test
    void getUserByUsernameAndPassword_withInvalidUsername_shouldReturnEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserByUsernameAndPassword("unknown", "pass");

        assertThat(result).isEmpty();
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void getUserByUsernameAndPassword_withInvalidPassword_shouldReturnEmpty() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", testUser.getPassword())).thenReturn(false);

        Optional<UserDto> result = userService.getUserByUsernameAndPassword("john_doe", "wrong");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteUser_shouldCallRepositoryDeleteById() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}