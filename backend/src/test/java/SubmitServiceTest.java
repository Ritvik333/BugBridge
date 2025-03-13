import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.demo.Model.Bug;
import com.example.demo.Model.Submit;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Repository.SubmitRepository;
import com.example.demo.Service.NotificationService;
import com.example.demo.Service.SubmitService;

class SubmitServiceTest {

    @Mock
    private SubmitRepository submitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BugRepository bugRepository;

    @InjectMocks
    private SubmitService submitService;

    @Mock  // This will automatically inject the mocks into the submitService
    private NotificationService notificationService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes the mocks
        
        // Making sure NotificationService is properly mocked
        doNothing().when(notificationService).createNotification(any(Long.class), any(String.class)); // It doesn't need to return anything, just prevent null error

    }

    // --- Tests for saveSubmission ---

    @Test
    void testSaveSubmissionSuccess() throws IOException {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        User user = new User();
        user.setId(userId);
        user.setUsername("john_doe"); // Set username for notification

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setLanguage("java");
        bug.setCreator(user); // User is the creator for approval logic

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        // Mock submitRepository.save to return the same Submit with an updated ID
        when(submitRepository.save(any(Submit.class))).thenAnswer(invocation -> {
            Submit submitArg = invocation.getArgument(0);
            submitArg.setId(10L); // Simulate setting the ID
            return submitArg;
        });
        // Mock notificationService to avoid real calls
        doNothing().when(notificationService).createNotification(anyLong(), anyString());

        // Act
        Submit submit = submitService.saveSubmission(userId, bugId, username, desc, code);
        System.out.println(submit);

        // Assert
        assertNotNull(submit);
        assertEquals(user, submit.getUser());
        assertEquals(bug, submit.getBug());
        assertEquals(desc, submit.getDescription());
        assertEquals("approved", submit.getApprovalStatus()); // Creator gets auto-approval
        String expectedPath = "uploads/" + userId + "_" + username + "/submissions/" + userId + "_" + bugId + "_10.java";
        assertEquals(expectedPath, submit.getCodeFilePath());
        verify(submitRepository, times(2)).save(any(Submit.class));
        verify(notificationService, times(2)).createNotification(anyLong(), anyString());
    }

    @Test
    void testSaveSubmissionUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            submitService.saveSubmission(userId, bugId, username, desc, code);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testSaveSubmissionBugNotFound() {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            submitService.saveSubmission(userId, bugId, username, desc, code);
        });
        assertEquals("Bug not found", exception.getMessage());
    }

    // --- Tests for mapLanguageToExtension ---

    @Test
    void testMapLanguageToExtension() {
        assertEquals(".java", submitService.mapLanguageToExtension("java"));
        assertEquals(".py", submitService.mapLanguageToExtension("python"));
        assertEquals(".js", submitService.mapLanguageToExtension("javascript"));
        assertEquals(".txt", submitService.mapLanguageToExtension("other"));
        assertEquals(".txt", submitService.mapLanguageToExtension(null));
    }

    // --- Tests for getSubmissionsForUserAndBug ---

    @Test
    void testGetSubmissionsForUserAndBugSuccess() {
        // Arrange
        Long userId = 1L;
        Long bugId = 2L;

        Submit submit1 = new Submit();
        submit1.setId(1L);
        Submit submit2 = new Submit();
        submit2.setId(2L);

        List<Submit> expectedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByUserIdAndBugId(userId, bugId)).thenReturn(expectedSubmissions);

        // Act
        List<Submit> result = submitService.getSubmissionsForUserAndBug(userId, bugId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedSubmissions, result);
        verify(submitRepository, times(1)).findByUserIdAndBugId(userId, bugId);
    }

    @Test
    void testGetSubmissionsForUserAndBugNoResults() {
        // Arrange
        Long userId = 1L;
        Long bugId = 2L;

        when(submitRepository.findByUserIdAndBugId(userId, bugId)).thenReturn(Collections.emptyList());

        // Act
        List<Submit> result = submitService.getSubmissionsForUserAndBug(userId, bugId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(submitRepository, times(1)).findByUserIdAndBugId(userId, bugId);
    }

    // --- Tests for findApprovedSubmissionsByBugId ---

   @Test
    void testFindApprovedSubmissionsByBugIdMultipleFromSameUser() {
        // Arrange
        Long bugId = 1L;
        User user1 = new User();
        user1.setId(1L);

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setStatus("open"); // Initial status

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(bug); // Associate with the same bugId
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneId.systemDefault()));
        submit1.setApprovalStatus("approved"); // Ensure approval status is set

        Submit submit2 = new Submit();
        submit2.setUser(user1);
        submit2.setBug(bug); // Associate with the same bugId
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneId.systemDefault()));
        submit2.setApprovalStatus("approved"); // Ensure approval status is set

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2); // Include both submissions
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug)); // Mock Bug lookup
        when(bugRepository.save(any(Bug.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(submit2, result.get(0)); // Should return the most recent submission
        assertEquals("Resolved", bug.getStatus()); // Verify bug status is updated
        verify(submitRepository).findByBugIdAndApprovalStatus(bugId, "approved");
        verify(bugRepository).findById(bugId);
        verify(bugRepository).save(bug);
    }

    @Test
    void testFindApprovedSubmissionsByBugIdDifferentUsers() {
        // Arrange
        Long bugId = 1L;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setStatus("open"); // Initial status

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(bug);
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneId.systemDefault()));
        submit1.setApprovalStatus("approved"); // Ensure approval status is set

        Submit submit2 = new Submit();
        submit2.setUser(user2);
        submit2.setBug(bug);
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneId.systemDefault()));
        submit2.setApprovalStatus("approved"); // Ensure approval status is set

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        when(bugRepository.save(any(Bug.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save to return the modified Bug

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(submit1));
        assertTrue(result.contains(submit2));
        assertEquals("Resolved", bug.getStatus()); // Verify bug status is updated
        verify(submitRepository).findByBugIdAndApprovalStatus(bugId, "approved");
        verify(bugRepository).findById(bugId);
        verify(bugRepository).save(bug);
    }

    @Test
    void testFindApprovedSubmissionsByBugIdNoResults() {
        // Arrange
        Long bugId = 1L;
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(Collections.emptyList());

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertTrue(result.isEmpty());
    }

    // --- Tests for getSubmissionById ---

    @Test
    void testGetSubmissionByIdFound() {
        // Arrange
        Long submissionId = 1L;
        Submit expectedSubmit = new Submit();
        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(expectedSubmit));

        // Act
        Submit result = submitService.getSubmissionById(submissionId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedSubmit, result);
    }

    @Test
    void testGetSubmissionByIdNotFound() {
        // Arrange
        Long submissionId = 1L;
        when(submitRepository.findById(submissionId)).thenReturn(Optional.empty());

        // Act
        Submit result = submitService.getSubmissionById(submissionId);

        // Assert
        assertNull(result);
    }
    @Test
    void testApproveSubmissionValidScenario() {
        // Arrange
        Long submissionId = 1L;
        Long bugCreatorId = 100L;
        Long submitterId = 200L;

        User bugCreator = new User();
        bugCreator.setId(bugCreatorId);

        User submitter = new User();
        submitter.setId(submitterId);

        Bug bug = new Bug();
        bug.setId(10L);
        bug.setCreator(bugCreator); // Bug creator

        Submit submission = new Submit();
        submission.setId(submissionId);
        submission.setUser(submitter);
        submission.setBug(bug);
        submission.setApprovalStatus("unapproved");

        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        // Act
        String result = submitService.approveSubmission(submissionId, bugCreatorId);

        // Assert
        assertEquals("Submission approved successfully.", result);
        assertEquals("approved", submission.getApprovalStatus());
        verify(submitRepository, times(1)).save(submission);

    }

    @Test
    void testApproveSubmissionBySubmitterShouldFail() {
        // Arrange
        Long submissionId = 1L;
        Long submitterId = 200L;
        
        User submitter = new User();
        submitter.setId(submitterId);  // Ensure ID is set

        User bugCreator = new User();
        bugCreator.setId(100L);  // Ensure bug creator ID is set

        Bug bug = new Bug();
        bug.setId(10L);
        bug.setCreator(bugCreator);  // Bug creator is properly assigned

        Submit submission = new Submit();
        submission.setId(submissionId);
        submission.setUser(submitter);
        submission.setBug(bug);
        submission.setApprovalStatus("unapproved");

        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        // Act
        String result = submitService.approveSubmission(submissionId, submitterId);

        // Assert
        assertEquals("Only the bug creator can approve submissions.", result);
        assertEquals("unapproved", submission.getApprovalStatus());
        verify(submitRepository, never()).save(any(Submit.class));
    }

    @Test
    void testApproveSubmissionByRandomUserShouldFail() {
        // Arrange
        Long submissionId = 1L;
        Long randomUserId = 999L;

        User bugCreator = new User();
        bugCreator.setId(100L);

        Bug bug = new Bug();
        bug.setId(10L);
        bug.setCreator(bugCreator); // Bug creator

        Submit submission = new Submit();
        submission.setId(submissionId);
        submission.setUser(new User());
        submission.setBug(bug);
        submission.setApprovalStatus("unapproved");

        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        // Act
        String result = submitService.approveSubmission(submissionId, randomUserId);

        // Assert
        assertEquals("Only the bug creator can approve submissions.", result);
        verify(submitRepository, never()).save(any(Submit.class));
    }

    @Test
    void testApproveSubmissionUserIdDoesNotExist() {
        // Arrange
        Long submissionId = 1L;
        Long approverId = 100L; // Approver ID

        when(submitRepository.findById(submissionId)).thenReturn(Optional.empty());

        // Act
        String result = submitService.approveSubmission(submissionId, approverId);

        // Assert
        assertEquals("Submission not found.", result);
        verify(submitRepository, never()).save(any(Submit.class));
    }

    @Test
    void testApproveSubmissionWithNullParameters() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> submitService.approveSubmission(null, 100L));
        assertThrows(NullPointerException.class, () -> submitService.approveSubmission(1L, null));
    }
}