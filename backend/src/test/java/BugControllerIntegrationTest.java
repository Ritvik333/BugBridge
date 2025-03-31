import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.DemoApplication;  // Replace with your main application class if needed
import com.example.demo.Controller.BugController;
import com.example.demo.Service.BugService;
import com.example.demo.Service.FileStorageService;
import com.example.demo.Service.UserService;
import com.example.demo.Service.CommentService; // Add this to satisfy nested dependency
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BugController.class)
@ContextConfiguration(classes = DemoApplication.class) // Specify your main configuration class here
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security filters for testing
public class BugControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock service dependencies so no actual DB or external resources are used.
    @MockBean
    private BugService bugService;

    @MockBean
    private UserService userService;

    @MockBean
    private FileStorageService fileStorageService;

    // Adding a mock for CommentService to satisfy dependency in the nested CommentController.
    @MockBean
    private CommentService commentService;

    // Test GET /api/bugs endpoint with no bugs available.
    @Test
    public void testGetBugsReturnsEmptyList() throws Exception {
        when(bugService.getBugs(anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bugs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // Test GET /api/bugs/{id} when bug is not found.
    @Test
    public void testGetBugByIdNotFound() throws Exception {
        when(bugService.getBugById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/bugs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
