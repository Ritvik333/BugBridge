// import java.util.Collections;

// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.example.demo.Controller.BugController;
// import com.example.demo.DemoApplication;
// import com.example.demo.Service.BugService;
// import com.example.demo.Service.CommentService;
// import com.example.demo.Service.FileStorageService;
// import com.example.demo.Service.UserService;
// import com.example.demo.dto.getBugsDto;

// @WebMvcTest(controllers = BugController.class)
// @ContextConfiguration(classes = DemoApplication.class)
// @AutoConfigureMockMvc(addFilters = false)
// public class BugControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private BugService bugService;

//     @MockBean
//     private UserService userService;

//     @MockBean
//     private FileStorageService fileStorageService;

//     @MockBean
//     private CommentService commentService;

//     @Test
//     public void testGetBugsReturnsEmptyList() throws Exception {
//         when(bugService.getBugs(any(getBugsDto.class))).thenReturn(Collections.emptyList());

//         mockMvc.perform(get("/api/bugs")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json("[]"));
//     }

//     @Test
//     public void testGetBugByIdNotFound() throws Exception {
//         when(bugService.getBugById(any(Long.class))).thenReturn(null);

//         mockMvc.perform(get("/api/bugs/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound());
//     }
// }
