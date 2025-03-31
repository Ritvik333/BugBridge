// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import com.example.demo.Controller.NotificationController;
// import com.example.demo.DemoApplication; // Replace with your main class if different
// import com.example.demo.Model.Notification;
// import com.example.demo.Service.NotificationService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.web.servlet.MockMvc;

// @WebMvcTest(NotificationController.class)
// @ContextConfiguration(classes = DemoApplication.class)
// @AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters for testing
// public class NotificationControllerIntegrationTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private NotificationService notificationService;

//     private ObjectMapper objectMapper = new ObjectMapper();

//     // Test GET /notifications/user/{userId} - returns all notifications for a user.
//     @Test
//     public void testGetUserNotifications() throws Exception {
//         Notification notification1 = new Notification();
//         notification1.setId(1L);
//         Notification notification2 = new Notification();
//         notification2.setId(2L);
//         List<Notification> notifications = Arrays.asList(notification1, notification2);

//         when(notificationService.getUserNotifications(anyLong())).thenReturn(notifications);

//         mockMvc.perform(get("/notifications/user/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0].id").value(1))
//                 .andExpect(jsonPath("$[1].id").value(2));
//     }

//     // Test GET /notifications/unread/{userId} - returns unread notifications wrapped in ResponseWrapper.
//     @Test
//     public void testGetUnreadNotifications() throws Exception {
//         Notification notification = new Notification();
//         notification.setId(1L);
//         List<Notification> notifications = Collections.singletonList(notification);

//         when(notificationService.getUnreadNotifications(anyLong())).thenReturn(notifications);

//         mockMvc.perform(get("/notifications/unread/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("success"))
//                 .andExpect(jsonPath("$.message").value("Unread notifications fetched"))
//                 .andExpect(jsonPath("$.body[0].id").value(1));
//     }

//     // Test POST /notifications/read/{userId} - mark all notifications as read.
//     @Test
//     public void testMarkAllAsRead() throws Exception {
//         // For this endpoint, simply simulate the call (do nothing)
//         Mockito.doNothing().when(notificationService).markAllAsRead(anyLong());

//         mockMvc.perform(post("/notifications/read/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("success"))
//                 .andExpect(jsonPath("$.message").value("All notifications marked as read"));
//     }

//     // Test PUT /notifications/read/{notificationId} - mark a single notification as read.
//     @Test
//     public void testMarkNotificationAsRead() throws Exception {
//         when(notificationService.markAsRead(anyLong())).thenReturn("Notification marked as read");

//         mockMvc.perform(put("/notifications/read/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("success"))
//                 .andExpect(jsonPath("$.message").value("Notification marked as read"));
//     }
// }
