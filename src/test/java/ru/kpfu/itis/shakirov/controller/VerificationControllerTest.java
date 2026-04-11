package ru.kpfu.itis.shakirov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.shakirov.service.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationController.class)
@WithMockUser
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void verify_withValidCode_shouldRedirectWithSuccessMessage() throws Exception {
        String code = "valid-code";
        when(userService.verifyUser(code)).thenReturn(true);

        mockMvc.perform(get("/verification").param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Email успешно подтверждён! Теперь вы можете войти."));

        verify(userService).verifyUser(code);
    }

    @Test
    void verify_withInvalidCode_shouldRedirectWithErrorMessage() throws Exception {
        String code = "invalid-code";
        when(userService.verifyUser(code)).thenReturn(false);

        mockMvc.perform(get("/verification").param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Неверный или просроченный код подтверждения."));

        verify(userService).verifyUser(code);
    }

    @Test
    void verify_whenServiceThrowsException_shouldRedirectWithGenericError() throws Exception {
        String code = "error-code";
        when(userService.verifyUser(code)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/verification").param("code", code))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Произошла ошибка при подтверждении. Попробуйте позже."));

        verify(userService).verifyUser(code);
    }

    @Test
    void verify_withoutCodeParam_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/verification"))
                .andExpect(status().isBadRequest());
    }
}