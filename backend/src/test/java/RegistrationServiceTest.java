
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.RegistrationService;

class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Tests for registerUser ---

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = registrationService.registerUser(user);

        // Assert: Single compound assertion that verifies all the expected user details.
        assertTrue(result != null &&
                        "john_doe".equals(result.getUsername()) &&
                        "john.doe@example.com".equals(result.getEmail()) &&
                        "encodedPassword123".equals(result.getPassword()),
                "Expected user to be non-null with username 'john_doe', email 'john.doe@example.com', and password 'encodedPassword123'.");

        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void testRegisterUserUsernameTaken() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(new User()));

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> registrationService.registerUser(user));

        // Assert: Combine all verifications into one compound assertion.
        assertAll("registerUserUsernameTaken",
                () -> assertEquals("Username already taken", exception.getMessage()),
                () -> verify(userRepository, times(1)).findByUsername("john_doe"),
                () -> verify(userRepository, never()).findByEmail(anyString()),
                () -> verify(passwordEncoder, never()).encode(anyString()),
                () -> verify(userRepository, never()).save(any(User.class))
        );
    }

    @Test
    void testRegisterUserEmailTaken() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new User()));

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> registrationService.registerUser(user));

        // Assert: Wrap all checks in a single assertAll block.
        assertAll("registerUserEmailTaken",
                () -> assertEquals("Email already taken", exception.getMessage()),
                () -> verify(userRepository, times(1)).findByUsername("john_doe"),
                () -> verify(userRepository, times(1)).findByEmail("john.doe@example.com"),
                () -> verify(passwordEncoder, never()).encode(anyString()),
                () -> verify(userRepository, never()).save(any(User.class))
        );
    }


    @Test
    void testRegisterUserInvalidEmail() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("invalid-email"); // Invalid email format
        user.setPassword("password123");

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("invalid-email")).thenReturn(Optional.empty());

        // Act & Assert: Combine the exception and message verification into one assertion
        assertEquals("Invalid email format",
                assertThrows(IllegalArgumentException.class, () -> registrationService.registerUser(user)).getMessage());

        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, times(1)).findByEmail("invalid-email");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void testRegisterUserPasswordTooShort() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("short"); // Less than 6 characters

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert combined into one assertion:
        assertEquals("Password must be at least 6 characters long",
                assertThrows(IllegalArgumentException.class, () -> registrationService.registerUser(user)).getMessage());

        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }


    // --- Tests for isValidEmail ---

    @Test
    void testIsValidEmailValid() {
        // Arrange
        String validEmail = "user@example.com";

        // Act
        boolean result = registrationService.isValidEmail(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidEmailInvalid() {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act
        boolean result = registrationService.isValidEmail(invalidEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidEmailWithSubdomain() {
        // Arrange
        String validEmail = "user@sub.domain.co.uk";

        // Act
        boolean result = registrationService.isValidEmail(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidEmailWithSpecialCharacters() {
        // Arrange
        String validEmail = "user+label@example.com";

        // Act
        boolean result = registrationService.isValidEmail(validEmail);

        // Assert
        assertTrue(result);
    }
}