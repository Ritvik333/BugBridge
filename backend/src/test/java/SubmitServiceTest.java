
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.example.demo.Service.NotificationService;
import com.example.demo.Service.SubmitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.Model.Bug;
import com.example.demo.Model.Submit;
import com.example.demo.Model.User;
import com.example.demo.Model.UserRepository;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Repository.SubmitRepository;

class SubmitServiceTest {

    @Mock
    private SubmitRepository submitRepository;

    @Mock
    private BugRepository bugRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SubmitService submitService;

    // A temporary storage path for file creation during tests
    private final String testStoragePath = "target/test-uploads";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Use reflection to change the private final storagePath field to our test folder.
        Field storagePathField = SubmitService.class.getDeclaredField("storagePath");
        storagePathField.setAccessible(true);
        storagePathField.set(submitService, testStoragePath);
    }

    // --- Tests for saveSubmission ---

    @Test
    void testSaveSubmissionSuccess_whenUserIsNotBugCreator() throws IOException {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        // Bug creator is different from userId.
        User creator = new User();
        creator.setId(2L);

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setCreator(creator);
        bug.setLanguage("java");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));

        // Simulate the submission save call assigning an ID to the submission.
        when(submitRepository.save(any(Submit.class))).thenAnswer(invocation -> {
            Submit s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(100L);
            }
            return s;
        });

        // Act
        Submit submit = submitService.saveSubmission(userId, bugId, username, desc, code);
        Path filePath = Paths.get(submit.getCodeFilePath());

        // Assert: Group all validations in a single compound assertion.
        assertAll("Submission success validations",
                () -> {
                    // Submission property assertions.
                    assertNotNull(submit, "Submission should not be null");
                    assertEquals(user, submit.getUser(), "User should match");
                    assertEquals(bug, submit.getBug(), "Bug should match");
                    assertEquals(desc, submit.getDescription(), "Description should match");
                    assertNotNull(submit.getCodeFilePath(), "Code file path should be set");
                    assertTrue(submit.getCodeFilePath().endsWith(".java"), "File extension should be .java");
                },
                () -> {
                    // File system assertions.
                    assertTrue(Files.exists(filePath), "File should exist at the absolute path");
                    String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                    assertEquals(code, fileContent, "File content should match the provided code");
                },
                () -> {
                    // Notification verifications.
                    verify(notificationService, times(1))
                            .createNotification(eq(userId), contains("has been submitted."));
                    verify(notificationService, times(1))
                            .createNotification(eq(creator.getId()), contains("by " + user.getUsername()));
                }
        );

        // Cleanup test file
        Files.deleteIfExists(filePath);
    }

    @Test
    void testSaveSubmissionSuccess_whenUserIsBugCreator() throws IOException {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "jane_doe";
        String desc = "Submission by bug creator";
        String code = "print('Hello world')";
        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        // Bug creator is the same as user.
        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setCreator(user);
        bug.setLanguage("python");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        when(submitRepository.save(any(Submit.class))).thenAnswer(invocation -> {
            Submit s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(200L);
            }
            return s;
        });

        // Act
        Submit submit = submitService.saveSubmission(userId, bugId, username, desc, code);

        // Assert: when the bug creator is the user, the status should be set to "approved".
        assertAll("Submission by bug creator validations",
                () -> assertNotNull(submit),
                () -> assertNotNull(submit.getCodeFilePath(), "Code file path should be set"),
                () -> assertTrue(submit.getCodeFilePath().endsWith(".py"), "File extension should be .py")
        );

        // Verify file contents
        Path filePath = Paths.get(submit.getCodeFilePath());
        assertTrue(Files.exists(filePath), "File should exist");
        String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        assertEquals(code, fileContent, "File content should match the code provided");

        // Verify notifications sent
        verify(notificationService, times(1))
                .createNotification(eq(userId), contains("has been submitted."));
        verify(notificationService, times(1))
                .createNotification(eq(userId), contains("by " + user.getUsername()));

        // Cleanup
        Files.deleteIfExists(filePath);
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

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                submitService.saveSubmission(userId, bugId, username, desc, code)
        );

        // Assert: Single compound assertion.
        assertTrue("User not found".equals(exception.getMessage()),
                "Expected exception message to be 'User not found'");
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
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                submitService.saveSubmission(userId, bugId, username, desc, code)
        );

        // Assert: Single compound assertion
        assertTrue("Bug not found".equals(exception.getMessage()),
                "Expected exception message to be 'Bug not found'");
    }


    @Test
    void testMapLanguageToExtension() {
        String[] languages = {"java", "python", "javascript", "other", null};
        String[] expectedExtensions = {".java", ".py", ".js", ".txt", ".txt"};

        String[] actualExtensions = Arrays.stream(languages)
                .map(lang -> submitService.mapLanguageToExtension(lang))
                .toArray(String[]::new);

        assertArrayEquals(expectedExtensions, actualExtensions, "Language mappings should match expected extensions");
    }


    @Test
    void testFindApprovedSubmissionsByBugIdMultipleFromSameUser() {
        // Arrange
        Long bugId = 1L;
        User user1 = new User();
        user1.setId(1L);

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setStatus("open");

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(bug);
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneId.systemDefault()));
        submit1.setApprovalStatus("approved");

        Submit submit2 = new Submit();
        submit2.setUser(user1);
        submit2.setBug(bug);
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneId.systemDefault()));
        submit2.setApprovalStatus("approved");

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        when(bugRepository.save(any(Bug.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert: Single compound assertion checking all conditions.
        assertTrue(result != null
                        && result.size() == 1
                        && result.get(0).equals(submit2)
                        && "Resolved".equals(bug.getStatus()),
                "Expected one submission per user (the most recent one) and bug status 'Resolved'");
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
        bug.setStatus("open");

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(bug);
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneId.systemDefault()));
        submit1.setApprovalStatus("approved");

        Submit submit2 = new Submit();
        submit2.setUser(user2);
        submit2.setBug(bug);
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneId.systemDefault()));
        submit2.setApprovalStatus("approved");

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        when(bugRepository.save(any(Bug.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert: Single compound assertion
        assertTrue(
                result != null &&
                        result.size() == 2 &&
                        result.contains(submit1) &&
                        result.contains(submit2) &&
                        "Resolved".equals(bug.getStatus()),
                "Expected 2 approved submissions (submit1 and submit2) and bug status updated to 'Resolved'"
        );
    }

    @Test
    void testFindApprovedSubmissionsByBugIdNoResults() {
        // Arrange
        Long bugId = 1L;
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(Collections.emptyList());

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty when no approved submissions exist");
    }

    @Test
    void testFindApprovedSubmissionsByBugId_BugNotFound() {
        // Arrange
        Long bugId = 1L;
        Submit submit = new Submit();
        User user = new User();
        user.setId(1L);
        submit.setUser(user);
        submit.setSubmittedAt(LocalDateTime.now());
        submit.setApprovalStatus("approved");
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(Collections.singletonList(submit));
        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            submitService.findApprovedSubmissionsByBugId(bugId);
        });
        assertEquals("Bug not found", ex.getMessage());
    }

    @Test
    void testGetSubmissionsForUserAndBugSuccess() {
        // Arrange
        Long userId = 1L;
        Long bugId = 2L;
        Submit s1 = new Submit();
        s1.setId(1L);
        Submit s2 = new Submit();
        s2.setId(2L);
        List<Submit> expected = Arrays.asList(s1, s2);
        when(submitRepository.findByUserIdAndBugId(userId, bugId)).thenReturn(expected);

        // Act
        List<Submit> result = submitService.getSubmissionsForUserAndBug(userId, bugId);

        // Assert: Single compound assertion checking non-null, size, and equality.
        assertTrue(result != null
                        && result.size() == 2
                        && result.equals(expected),
                "Expected result to be non-null, have 2 elements, and equal to the expected list");

        verify(submitRepository, times(1)).findByUserIdAndBugId(userId, bugId);
    }


    @Test
    void testGetSubmissionByIdFound() {
        // Arrange
        Long submissionId = 5L;
        Submit submit = new Submit();
        submit.setId(submissionId);
        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submit));

        // Act
        Submit result = submitService.getSubmissionById(submissionId);

        // Assert
        assertNotNull(result);
        assertEquals(submissionId, result.getId());
    }

    @Test
    void testGetSubmissionByIdNotFound() {
        // Arrange
        when(submitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Submit result = submitService.getSubmissionById(999L);

        // Assert
        assertNull(result);
    }

    // --- Tests for approveSubmission ---
    @Test
    void testApproveSubmission_NullArguments() {
        assertAll("Null argument tests",
                () -> assertThrows(NullPointerException.class, () -> submitService.approveSubmission(null, 1L)),
                () -> assertThrows(NullPointerException.class, () -> submitService.approveSubmission(1L, null))
        );
    }

    @Test
    void testApproveSubmission_SubmissionNotFound() {
        when(submitRepository.findById(anyLong())).thenReturn(Optional.empty());
        String result = submitService.approveSubmission(1L, 1L);
        assertEquals("Submission not found.", result);
    }

    @Test
    void testApproveSubmission_UserNotBugCreator() {
        Submit submission = new Submit();
        submission.setId(1L);
        Bug bug = new Bug();
        User creator = new User();
        creator.setId(10L);
        bug.setCreator(creator);
        submission.setBug(bug);
        when(submitRepository.findById(1L)).thenReturn(Optional.of(submission));

        String result = submitService.approveSubmission(1L, 5L);
        assertEquals("Only the bug creator can approve submissions.", result);
        verify(submitRepository, never()).save(any(Submit.class));
        verify(notificationService, never()).createNotification(anyLong(), anyString());
    }

    @Test
    void testApproveSubmission_Success() {
        Submit submission = new Submit();
        submission.setId(1L);
        Bug bug = new Bug();
        User creator = new User();
        creator.setId(5L);
        bug.setCreator(creator);
        bug.setId(100L);
        submission.setBug(bug);
        User submitter = new User();
        submitter.setId(2L);
        submission.setUser(submitter);

        when(submitRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submitRepository.save(any(Submit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = submitService.approveSubmission(1L, 5L);
        assertAll("Approve submission validations",
                () -> assertEquals("Submission approved successfully.", result),
                () -> assertEquals("approved", submission.getApprovalStatus())
        );
        verify(notificationService).createNotification(eq(submitter.getId()), anyString());
    }

    // --- Tests for rejectSubmission ---
    @Test
    void testRejectSubmission_NullArguments() {
        assertAll("Reject submission null arguments",
                () -> assertThrows(NullPointerException.class, () -> submitService.rejectSubmission(null, 1L)),
                () -> assertThrows(NullPointerException.class, () -> submitService.rejectSubmission(1L, null))
        );
    }

    @Test
    void testRejectSubmission_SubmissionNotFound() {
        when(submitRepository.findById(anyLong())).thenReturn(Optional.empty());
        String result = submitService.rejectSubmission(1L, 1L);
        assertEquals("Submission not found.", result);
    }

    @Test
    void testRejectSubmission_UserNotBugCreator() {
        Submit submission = new Submit();
        submission.setId(1L);
        Bug bug = new Bug();
        User creator = new User();
        creator.setId(10L);
        bug.setCreator(creator);
        submission.setBug(bug);
        when(submitRepository.findById(1L)).thenReturn(Optional.of(submission));

        String result = submitService.rejectSubmission(1L, 5L);
        assertEquals("Only the bug creator can reject submissions.", result);
        verify(submitRepository, never()).save(any(Submit.class));
        verify(notificationService, never()).createNotification(anyLong(), anyString());
    }

    @Test
    void testRejectSubmission_Success() {
        Submit submission = new Submit();
        submission.setId(1L);
        Bug bug = new Bug();
        User creator = new User();
        creator.setId(5L);
        bug.setCreator(creator);
        bug.setId(100L);
        submission.setBug(bug);
        User submitter = new User();
        submitter.setId(2L);
        submission.setUser(submitter);

        when(submitRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submitRepository.save(any(Submit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = submitService.rejectSubmission(1L, 5L);
        assertAll("Reject submission validations",
                () -> assertEquals("Submission rejected successfully.", result),
                () -> assertEquals("rejected", submission.getApprovalStatus())
        );
        verify(notificationService).createNotification(eq(submitter.getId()), anyString());
    }

    // --- Tests for getUnapprovedSubmissions and getApprovedSubmissions ---
    @Test
    void testGetUnapprovedSubmissions() {
        Submit s1 = new Submit();
        s1.setId(1L);
        List<Submit> unapproved = Collections.singletonList(s1);
        when(submitRepository.findByApprovalStatus("unapproved")).thenReturn(unapproved);

        List<Submit> result = submitService.getUnapprovedSubmissions();
        assertEquals(unapproved, result);
    }

    @Test
    void testGetApprovedSubmissions() {
        Submit s1 = new Submit();
        s1.setId(1L);
        List<Submit> approved = Collections.singletonList(s1);
        when(submitRepository.findByApprovalStatus("approved")).thenReturn(approved);

        List<Submit> result = submitService.getApprovedSubmissions();
        assertEquals(approved, result);
    }

    // --- Test for getSubmissionsForCreatedBugs ---
    @Test
    void testGetSubmissionsForCreatedBugs() {
        Long creatorId = 1L;
        Bug bug1 = new Bug();
        bug1.setId(10L);
        Bug bug2 = new Bug();
        bug2.setId(20L);
        List<Bug> createdBugs = Arrays.asList(bug1, bug2);

        Submit s1 = new Submit();
        s1.setId(1L);
        s1.setBug(bug1);
        Submit s2 = new Submit();
        s2.setId(2L);
        s2.setBug(bug2);

        when(bugRepository.findByCreatorId(creatorId)).thenReturn(createdBugs);
        when(submitRepository.findByBugId(10L)).thenReturn(Collections.singletonList(s1));
        when(submitRepository.findByBugId(20L)).thenReturn(Collections.singletonList(s2));

        List<Submit> result = submitService.getSubmissionsForCreatedBugs(creatorId);
        assertAll("Submissions for created bugs",
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(s1)),
                () -> assertTrue(result.contains(s2))
        );
    }

    // --- Test for getAllSubmissionsForUser ---
    @Test
    void testGetAllSubmissionsForUser() {
        Long userId = 1L;
        // Submissions by the user (for bugs that are not created by the user)
        Bug bug1 = new Bug();
        bug1.setId(10L);
        Bug bug2 = new Bug();
        bug2.setId(20L);
        Bug bug3 = new Bug();
        bug3.setId(30L);
        Submit s1 = new Submit();
        s1.setId(1L);
        s1.setBug(bug1);
        Submit s2 = new Submit();
        s2.setId(2L);
        s2.setBug(bug2);
        Submit s3 = new Submit();
        s3.setId(3L);
        s3.setBug(bug3);
        List<Submit> userSubmissions = Arrays.asList(s1, s2, s3);
        when(submitRepository.findByUserId(userId)).thenReturn(userSubmissions);

        // Bugs created by the user (assume bug2 is created by the user)
        Bug createdBug = new Bug();
        createdBug.setId(20L);
        List<Bug> createdBugs = Collections.singletonList(createdBug);
        when(bugRepository.findByCreatorId(userId)).thenReturn(createdBugs);

        List<Submit> result = submitService.getAllSubmissionsForUser(userId);
        // s2 should be filtered out because its bug was created by the user.
        assertAll("Filtered submissions for user",
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(s1)),
                () -> assertTrue(result.contains(s3)),
                () -> assertFalse(result.contains(s2))
        );
    }
}
