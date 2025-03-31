// import com.example.demo.DemoApplication; // Replace with your main application class if needed
// import com.example.demo.Security.UserAuthenticationProvider;
// import com.example.demo.Service.PasswordResetService;
// import com.example.demo.Service.UserService;
// import com.example.demo.dto.CredentialsDto;
// import com.example.demo.dto.SignUpDto;
// import com.example.demo.dto.UserDto;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest(classes = DemoApplication.class,
//         webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
// public class AuthControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     // Mocks for all dependencies (no actual DB or external services are used)
//     @MockBean
//     private UserService userService;

//     @MockBean
//     private UserAuthenticationProvider userAuthenticationProvider;

//     @MockBean
//     private PasswordResetService passwordResetService;

//     private ObjectMapper objectMapper = new ObjectMapper();


// //    @Test
// //    public void testVerifyEmail_Success() throws Exception {
// //        // Arrange
// //        Mockito.when(userService.verifyEmail(anyString(), Mockito.anyLong()))
// //                .thenReturn(true);
// //        String requestJson = "{\"otp\":\"123456\", \"userId\":\"1\"}";
// //
// //        // Act & Assert
// //        mockMvc.perform(post("/auth/verify-email")
// //                        .contentType(MediaType.APPLICATION_JSON)
// //                        .content(requestJson))
// //                .andExpect(status().isOk())
// //                .andExpect(jsonPath("$.status").value("success"))
// //                .andExpect(jsonPath("$.message").value("Email verified successfully"));
// //    }
// //
// //    @Test
// //    public void testVerifyEmail_Failure() throws Exception {
// //        // Arrange
// //        Mockito.when(userService.verifyEmail(anyString(), Mockito.anyLong()))
// //                .thenReturn(false);
// //        String requestJson = "{\"otp\":\"123456\", \"userId\":\"1\"}";
// //
// //        // Act & Assert
// //        mockMvc.perform(post("/auth/verify-email")
// //                        .contentType(MediaType.APPLICATION_JSON)
// //                        .content(requestJson))
// //                .andExpect(status().isBadRequest())
// //                .andExpect(jsonPath("$.status").value("error"))
// //                .andExpect(jsonPath("$.message").value("Invalid or expired OTP"));
// //    }
// }
