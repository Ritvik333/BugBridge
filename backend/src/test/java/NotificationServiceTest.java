import com.example.demo.Model.Notification;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        // Arrange
        Long userId = 1L;
        String message = "New bug assigned to you";
        Notification notification = new Notification(userId, message);

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.createNotification(userId, message);

        // Assert: Capture the notification and verify its properties.
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        Notification savedNotification = captor.getValue();

        assertAll("Notification properties",
                () -> assertEquals(userId, savedNotification.getUserId(), "User ID should match"),
                () -> assertEquals(message, savedNotification.getMessage(), "Message should match")
        );
    }


    @Test
    void testGetUserNotifications() {
        // Arrange
        Long userId = 1L;
        Notification notification1 = new Notification(userId, "Message 1");
        Notification notification2 = new Notification(userId, "Message 2");

        List<Notification> mockNotifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserId(userId)).thenReturn(mockNotifications);

        // Act
        List<Notification> result = notificationService.getUserNotifications(userId);

        // Assert: Use a single compound assertion to verify all conditions.
        assertAll("User notifications validation",
                () -> assertEquals(2, result.size(), "Expected 2 notifications"),
                () -> assertEquals("Message 1", result.get(0).getMessage(), "First message should be 'Message 1'"),
                () -> assertEquals("Message 2", result.get(1).getMessage(), "Second message should be 'Message 2'")
        );
        verify(notificationRepository, times(1)).findByUserId(userId);
    }


    @Test
    void testGetUnreadNotifications() {
        // Arrange
        Long userId = 1L;
        Notification notification1 = new Notification(userId, "Unread message 1");
        Notification notification2 = new Notification(userId, "Unread message 2");
        notification1.setRead(false);
        notification2.setRead(false);

        List<Notification> mockUnreadNotifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserIdAndIsReadFalse(userId)).thenReturn(mockUnreadNotifications);

        // Act
        List<Notification> result = notificationService.getUnreadNotifications(userId);

        // Assert: Combine all assertions into one compound assertion.
        assertAll("Unread notifications validation",
                () -> assertEquals(2, result.size(), "Expected 2 notifications"),
                () -> assertFalse(result.get(0).isRead(), "First notification should be unread"),
                () -> assertFalse(result.get(1).isRead(), "Second notification should be unread")
        );
        verify(notificationRepository, times(1)).findByUserIdAndIsReadFalse(userId);
    }


    @Test
    void testMarkAllAsRead() {
        // Arrange
        Long userId = 1L;
        Notification notification1 = new Notification(userId, "Unread message 1");
        Notification notification2 = new Notification(userId, "Unread message 2");
        notification1.setRead(false);
        notification2.setRead(false);

        List<Notification> mockUnreadNotifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserIdAndIsReadFalse(userId)).thenReturn(mockUnreadNotifications);

        // Act
        notificationService.markAllAsRead(userId);

        // Assert: Combine both assertions into one compound assertion.
        assertAll("Verify all notifications are marked as read",
                () -> assertTrue(notification1.isRead(), "Notification1 should be marked as read"),
                () -> assertTrue(notification2.isRead(), "Notification2 should be marked as read")
        );
        verify(notificationRepository, times(1)).saveAll(mockUnreadNotifications);
    }


    @Test
    void testMarkNotificationAsRead_ValidCase() {
        // Arrange
        Long notificationId = 1L;
        Notification notification = new Notification(1L, "Some message");
        notification.setRead(false);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // Act
        String result = notificationService.markAsRead(notificationId);

        // Assert: All checks combined in one compound assertion.
        assertAll("Mark notification as read",
                () -> assertEquals("Notification marked as read", result),
                () -> assertTrue(notification.isRead())
        );
        verify(notificationRepository, times(1)).save(notification);
    }


    @Test
    void testMarkNotificationAsRead_NotFound() {
        // Arrange
        Long notificationId = 99L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act
        String result = notificationService.markAsRead(notificationId);

        // Assert
        assertEquals("Notification not found", result);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
