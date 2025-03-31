// import com.example.demo.Controller.DraftController;
// import com.example.demo.DemoApplication; // Replace with your main application class if different
// import com.example.demo.Model.Draft;
// import com.example.demo.Model.User;
// import com.example.demo.Service.DraftService;
// import com.example.demo.Service.UserService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.Collections;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.ArgumentMatchers.anyLong;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(DraftController.class)
// @ContextConfiguration(classes = DemoApplication.class)  // Specify your main configuration class here
// @AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
// public class DraftControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     // Mock service dependencies to avoid any actual DB or file system interactions.
//     @MockBean
//     private UserService userService;

//     @MockBean
//     private DraftService draftService;

//     private ObjectMapper objectMapper = new ObjectMapper();

//     // Test POST /drafts/save endpoint
//     @Test
//     public void testSaveDraftSuccess() throws Exception {
//         // Prepare a dummy JSON request.
//         String requestJson = "{\"userId\":1, \"bugId\":1, \"code\":\"test code\"}";

//         // Prepare dummy User and Draft objects.
//         User dummyUser = new User();
//         dummyUser.setId(1L);
//         dummyUser.setUsername("testuser");

//         Draft dummyDraft = new Draft();
//         dummyDraft.setId(100L);
//         // Note: No call to setCode() is made, as your Draft model does not include that setter.

//         // Stub the service methods.
//         Mockito.when(userService.getUserById(1L)).thenReturn(dummyUser);
//         Mockito.when(draftService.saveDraftFile(eq(1L), eq(1L), eq("testuser"), eq("test code")))
//                 .thenReturn(dummyDraft);

//         mockMvc.perform(post("/drafts/save")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(requestJson))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("success"))
//                 .andExpect(jsonPath("$.message").value("Draft saved successfully"))
//                 .andExpect(jsonPath("$.body.id").value(100));
//     }

//     // Test GET /drafts/user/{userId} endpoint returning an empty list.
//     @Test
//     public void testGetUserDraftsEmpty() throws Exception {
//         Mockito.when(draftService.getDraftsForUser(1L))
//                 .thenReturn(Collections.emptyList());

//         mockMvc.perform(get("/drafts/user/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("success"))
//                 .andExpect(jsonPath("$.message").value("Fetched drafts successfully"))
//                 .andExpect(jsonPath("$.body").isEmpty());
//     }

//     // Test GET /drafts/file/{userId}/{username}/{language}/{filename} endpoint when file is not found.
//     @Test
//     public void testGetFileContentNotFound() throws Exception {
//         // No file is created in the test environment, so a 404 response is expected.
//         mockMvc.perform(get("/drafts/file/1/testuser/java/testfile.txt")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound());
//     }
// }
