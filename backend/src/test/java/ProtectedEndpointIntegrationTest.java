// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;  // Replace with your main application class
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.example.demo.Controller.ProtectedEndpoint;
// import com.example.demo.DemoApplication;

// @WebMvcTest(controllers = ProtectedEndpoint.class)
// @ContextConfiguration(classes = DemoApplication.class)  // Specify your main config class here
// @AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
// public class ProtectedEndpointIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Test
//     public void testGetProtectedData() throws Exception {
//         mockMvc.perform(get("/api/protected-endpoint")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("This is a protected resource. You have access!"));
//     }
// }
