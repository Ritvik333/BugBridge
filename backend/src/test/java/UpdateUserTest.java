import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.PasswordResetTokenRepository;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.dto.UserDto;
import com.example.demo.exceptions.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

class UpdateUserTest {

    @InjectMocks
    @Spy
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Fully initialized test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashed_password");
    }

    // Test Updating Username Only
    @Test
    void testUpdateUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto updateDto = new UserDto();
        updateDto.setUsername("new_username");

        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        assertEquals("success", response.getStatus());
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("new_username", testUser.getUsername());
        verify(userRepository).save(testUser);
    }

    // Test Updating Email (Should Send OTP)
    @Test
    void testUpdateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        doReturn("Verification email sent").when(userService).createEmailVerificationToken(1L, "new@example.com");

        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");

        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        assertEquals("success", response.getStatus());
        assertEquals("Email verification OTP Sent. Click 'Save Changes' after verification.", response.getMessage());
        verify(userService).createEmailVerificationToken(1L, "new@example.com"); //Ensure it was called
    }

    // Test Updating Password Without OTP
    @Test
    void testUpdatePasswordWithoutOTP() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("new_secure_password")).thenReturn("hashed_new_password");

        UserDto updateDto = new UserDto();
        updateDto.setPassword("new_secure_password");

        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        assertEquals("success", response.getStatus());
        assertEquals("User updated successfully", response.getMessage());
        assertEquals("hashed_new_password", testUser.getPassword()); // Ensure password is updated
        verify(userRepository).save(testUser);
    }

    // Test Updating Without Any Fields (Should Fail)
    @Test
    void testUpdateWithoutFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto updateDto = new UserDto(); // Empty DTO

        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUserAccount(1L, updateDto);
        });

        assertEquals("No valid fields to update", exception.getMessage());
    }

    // Test Verifying Email With OTP
    @Test
    void testVerifyEmailWithOTP() {
        PasswordResetToken token = new PasswordResetToken("123456", testUser, LocalDateTime.now().plusHours(1), "new@example.com");

        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        doReturn(true).when(userService).verifyEmail("123456", 1L);

        boolean result = userService.verifyEmail("123456", 1L);

        assertTrue(result);
        verify(userService).verifyEmail("123456", 1L);
    }
}