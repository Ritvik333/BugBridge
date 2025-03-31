

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.User;
import com.example.demo.Repository.PasswordResetTokenRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserMapper;
import com.example.demo.Service.UserService;
import com.example.demo.dto.CredentialsDto;
import com.example.demo.dto.ResponseWrapper;
import com.example.demo.dto.SignUpDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exceptions.AppException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;

class UserServiceTest {

    @InjectMocks
    @Spy
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashed_password");
    }

    // --- Tests for login ---
    @Test
    void testLoginSuccess() {
        CredentialsDto credentials = new CredentialsDto("test@example.com", "password".toCharArray());
        UserDto expectedDto = new UserDto(1L, "test_user", "test@example.com", null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(CharBuffer.wrap("password".toCharArray()), "hashed_password")).thenReturn(true);
        when(userMapper.toUserDto(testUser)).thenReturn(expectedDto);

        UserDto result = userService.login(credentials);

        // Single compound assertion verifying that result is not null and equals expectedDto.
        assertTrue(result != null && result.equals(expectedDto),
                "Expected a non-null result that matches the expected DTO");

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches(any(CharBuffer.class), eq("hashed_password"));
    }

    @Test
    void testLoginUnknownUser() {
        CredentialsDto credentials = new CredentialsDto("unknown@example.com", "password".toCharArray());
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.login(credentials));

        assertTrue("Unknown user".equals(exception.getMessage())
                        && HttpStatus.NOT_FOUND.equals(exception.getStatus()),
                "Expected exception with message 'Unknown user' and status NOT_FOUND");

        verify(userRepository).findByEmail("unknown@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }


    @Test
    void testLoginInvalidPassword() {
        CredentialsDto credentials = new CredentialsDto("test@example.com", "wrong".toCharArray());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(CharBuffer.wrap("wrong".toCharArray()), "hashed_password")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> userService.login(credentials));

        assertTrue("Invalid password".equals(exception.getMessage()) &&
                        HttpStatus.BAD_REQUEST.equals(exception.getStatus()),
                "Expected exception with message 'Invalid password' and status BAD_REQUEST");

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches(any(CharBuffer.class), eq("hashed_password"));
    }


    // --- Tests for register ---
    @Test
    void testRegisterSuccess() {
        SignUpDto signUpDto = new SignUpDto("test_user", "test@example.com", "password".toCharArray());
        User newUser = new User();
        newUser.setUsername("test_user");
        newUser.setEmail("test@example.com");
        UserDto expectedDto = new UserDto(1L, "test_user", "test@example.com", null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.signUpToUser(signUpDto)).thenReturn(newUser);
        when(passwordEncoder.encode(CharBuffer.wrap("password".toCharArray()))).thenReturn("hashed_password");
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userMapper.toUserDto(newUser)).thenReturn(expectedDto);

        UserDto result = userService.register(signUpDto);

        // Single compound assertion verifying that result is not null, equals expectedDto,
        // and that newUser's password is updated as expected.
        assertTrue(result != null
                        && expectedDto.equals(result)
                        && "hashed_password".equals(newUser.getPassword()),
                "Register success: result must be non-null, equal to expectedDto, and newUser password must be 'hashed_password'");

        verify(userRepository).save(newUser);
    }

    @Test
    void testRegisterEmailTaken() {
        SignUpDto signUpDto = new SignUpDto("test_user", "test@example.com", "password".toCharArray());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        AppException exception = assertThrows(AppException.class, () -> userService.register(signUpDto));

        // Combine all conditions into one compound boolean.
        boolean condition = "Login already exists".equals(exception.getMessage())
                && HttpStatus.BAD_REQUEST.equals(exception.getStatus())
                && noUserWasSaved();

        assertTrue(condition, "Expected exception with message 'Login already exists', status BAD_REQUEST, and that no user is saved");
    }

    private boolean noUserWasSaved() {
        try {
            verify(userRepository, never()).save(any(User.class));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }


    @Test
    void testRegisterNullDto() {
        AppException exception = assertThrows(AppException.class, () -> userService.register(null));
        assertAll("Register null DTO",
                () -> assertEquals("User data cannot be null", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
        verify(userRepository, never()).findByEmail(anyString());
    }

    // --- Tests for findByLogin ---
    @Test
    void testFindByLoginSuccess() {
        UserDto expectedDto = new UserDto(1L, "test_user", "test@example.com", null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDto(testUser)).thenReturn(expectedDto);

        UserDto result = userService.findByLogin("test@example.com");

        // Single compound assertion
        assertTrue(result != null && result.equals(expectedDto),
                "Expected result to be non-null and equal to the expected DTO");

        verify(userRepository).findByEmail("test@example.com");
    }


    @Test
    void testFindByLoginNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        AppException exception = assertThrows(AppException.class, () -> userService.findByLogin("unknown@example.com"));

        assertAll("findByLogin not found",
                () -> {
                    assertEquals("Unknown user", exception.getMessage());
                    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
                    verify(userRepository).findByEmail("unknown@example.com");
                }
        );
    }



    // --- Tests for getUserById ---
    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(1L);

        assertAll("Verify getUserById success",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(testUser, result, "Returned user should match testUser")
        );

        verify(userRepository).findById(1L);
    }


    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));

        assertAll("Verify exception message and repository call",
                () -> assertEquals("User with ID 1 not found", exception.getMessage()),
                () -> verify(userRepository).findById(1L)
        );
    }


    // --- Tests for getUsersWithBugs ---
    @Test
    void testGetUsersWithBugsSuccess() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findUsersWithBugs()).thenReturn(users);
        List<User> result = userService.getUsersWithBugs();
        assertTrue(result != null && result.size() == 2 && result.equals(users),
                "getUsersWithBugs success: result should not be null, have size 2, and match expected users");
        verify(userRepository).findUsersWithBugs();
    }


    // --- Tests for getUserDetails ---
    @Test
    void testGetUserDetailsSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        UserDto result = userService.getUserDetails(1L);

        assertAll("getUserDetails success",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1L, result.getId(), "User ID should match"),
                () -> assertEquals("test_user", result.getUsername(), "Username should match"),
                () -> assertEquals("test@example.com", result.getEmail(), "Email should match"),
                () -> assertNull(result.getPassword(), "Password should be null")
        );

        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserDetailsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        AppException exception = assertThrows(AppException.class, () -> userService.getUserDetails(1L));

        assertAll("getUserDetails not found",
                () -> assertEquals("User not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> verify(userRepository, times(1)).findById(1L)
        );
    }

    // --- Tests for updateUserAccount ---
    @Test
    void testUpdateUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        UserDto updateDto = new UserDto();
        updateDto.setUsername("new_username");
        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        // Single compound assertion checking all conditions.
        assertTrue("success".equals(response.getStatus())
                        && "User updated successfully".equals(response.getMessage())
                        && "new_username".equals(testUser.getUsername()),
                "Expected status 'success', message 'User updated successfully', and updated username 'new_username'");

        verify(userRepository).save(testUser);
    }

    @Test
    void testUpdateEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        // Spy on createEmailVerificationToken to return a known string.
        doReturn("Verification email sent").when(userService).createEmailVerificationToken(1L, "new@example.com");
        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");
        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        assertTrue("success".equals(response.getStatus())
                        && "Email verification OTP Sent. Click 'Save Changes' after verification.".equals(response.getMessage()),
                "Expected status 'success' and message 'Email verification OTP Sent. Click 'Save Changes' after verification.'");

        verify(userService).createEmailVerificationToken(1L, "new@example.com");
    }


    @Test
    void testUpdatePasswordWithoutOTP() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("new_secure_password")).thenReturn("hashed_new_password");
        UserDto updateDto = new UserDto();
        updateDto.setPassword("new_secure_password");
        ResponseWrapper<String> response = userService.updateUserAccount(1L, updateDto);

        // Single compound assertion checking all conditions together.
        assertTrue("success".equals(response.getStatus())
                        && "User updated successfully".equals(response.getMessage())
                        && "hashed_new_password".equals(testUser.getPassword()),
                "Expected status 'success', message 'User updated successfully', and password 'hashed_new_password'");

        verify(userRepository).save(testUser);
    }


    @Test
    void testUpdateWithoutFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        UserDto updateDto = new UserDto();
        AppException exception = assertThrows(AppException.class, () -> userService.updateUserAccount(1L, updateDto));

        // Single compound assertion to check both exception message and status
        assertTrue("No valid fields to update".equals(exception.getMessage())
                        && HttpStatus.BAD_REQUEST.equals(exception.getStatus()),
                "Expected exception with message 'No valid fields to update' and status BAD_REQUEST");

        verify(userRepository, never()).save(any());
    }


    // --- Tests for createEmailVerificationToken ---
    @Test
    void testCreateEmailVerificationTokenSuccess_NoExistingToken() throws MessagingException, UnsupportedEncodingException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        // Stub saving the token.
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Stub mail sender
        MimeMessage dummyMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(dummyMessage);

        String result = userService.createEmailVerificationToken(1L, "new@example.com");
        assertEquals("Verification email sent", result);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testCreateEmailVerificationTokenSuccess_WithExistingToken() throws MessagingException, UnsupportedEncodingException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        PasswordResetToken existingToken = new PasswordResetToken("000000", testUser, LocalDateTime.now().plusHours(1), "old@example.com");
        when(tokenRepository.findByUser(testUser)).thenReturn(Optional.of(existingToken));
        // Stub deletion of existing token.
        doNothing().when(tokenRepository).delete(existingToken);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MimeMessage dummyMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(dummyMessage);

        String result = userService.createEmailVerificationToken(1L, "new@example.com");
        assertEquals("Verification email sent", result);
        verify(tokenRepository).delete(existingToken);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testCreateEmailVerificationTokenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        String result = userService.createEmailVerificationToken(1L, "new@example.com");
        assertEquals("User not found", result);
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    // --- Tests for verifyEmail ---
    @Test
    void testVerifyEmail_TokenNotFound() {
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.empty());
        boolean result = userService.verifyEmail("123456", 1L);
        assertFalse(result);
        verify(tokenRepository).findByToken("123456");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testVerifyEmail_ExpiredToken() {
        PasswordResetToken token = new PasswordResetToken("123456", testUser, LocalDateTime.now().minusHours(1), "new@example.com");
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        boolean result = userService.verifyEmail("123456", 1L);
        assertFalse(result);
        verify(tokenRepository).findByToken("123456");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testVerifyEmail_WrongUser() {
        // Create a token for a different user.
        User otherUser = new User();
        otherUser.setId(2L);
        PasswordResetToken token = new PasswordResetToken("123456", otherUser, LocalDateTime.now().plusHours(1), "new@example.com");
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        boolean result = userService.verifyEmail("123456", 1L);
        assertFalse(result);
        verify(tokenRepository).findByToken("123456");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testVerifyEmail_NullNewEmail() {
        PasswordResetToken token = new PasswordResetToken("123456", testUser, LocalDateTime.now().plusHours(1), null);
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        boolean result = userService.verifyEmail("123456", 1L);
        assertFalse(result);
        verify(tokenRepository).findByToken("123456");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testVerifyEmail_Success() {
        PasswordResetToken token = new PasswordResetToken("123456", testUser, LocalDateTime.now().plusHours(1), "new@example.com");
        when(tokenRepository.findByToken("123456")).thenReturn(Optional.of(token));
        when(userRepository.save(testUser)).thenReturn(testUser);
        doNothing().when(tokenRepository).delete(token);

        boolean result = userService.verifyEmail("123456", 1L);
        assertTrue(result);
        assertEquals("new@example.com", testUser.getPendingEmail());
        verify(userRepository).save(testUser);
        verify(tokenRepository).delete(token);
    }
}
