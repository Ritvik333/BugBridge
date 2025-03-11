import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        Bug bug = new Bug();
        bug.setId(bugId);
        bug.setLanguage("java");
        bug.setCreator(user); // User is the creator for approval logic

        Submit savedSubmit = new Submit();
        savedSubmit.setId(10L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        when(submitRepository.save(any(Submit.class))).thenReturn(savedSubmit);

        // Act
        Submit submit = submitService.saveSubmission(userId, bugId, username, desc, code);
        System.out.println(submit);
        // Assert
        assertNotNull(submit);
        assertEquals(user, submit.getUser());
        assertEquals(bug, submit.getBug());
        assertEquals(desc, submit.getDescription());
        assertEquals("approved", submit.getApprovalStatus()); // Assuming creator gets auto-approval
        String expectedPath = "uploads/" + userId + "_" + username + "/submissions/" + userId + "_" + bugId + "_" + savedSubmit.getId() + ".java";
        assertEquals(expectedPath, submit.getCodeFilePath());
        verify(submitRepository, times(2)).save(any(Submit.class)); // save() called twice in service
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

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(new Bug());
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), java.time.ZoneId.systemDefault()));

        Submit submit2 = new Submit();
        submit2.setUser(user1);
        submit2.setBug(new Bug());
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), java.time.ZoneId.systemDefault()));

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(submit2, result.get(0)); // Should return the most recent submission
        verify(submitRepository).findByBugIdAndApprovalStatus(bugId, "approved");
    }

    @Test
    void testFindApprovedSubmissionsByBugIdDifferentUsers() {
        // Arrange
        Long bugId = 1L;
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        Submit submit1 = new Submit();
        submit1.setUser(user1);
        submit1.setBug(new Bug());
        submit1.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(1000L), java.time.ZoneId.systemDefault()));

        Submit submit2 = new Submit();
        submit2.setUser(user2);
        submit2.setBug(new Bug());
        submit2.setSubmittedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(2000L), java.time.ZoneId.systemDefault()));

        List<Submit> approvedSubmissions = Arrays.asList(submit1, submit2);
        when(submitRepository.findByBugIdAndApprovalStatus(bugId, "approved")).thenReturn(approvedSubmissions);

        // Act
        List<Submit> result = submitService.findApprovedSubmissionsByBugId(bugId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(submit1));
        assertTrue(result.contains(submit2));
        verify(submitRepository).findByBugIdAndApprovalStatus(bugId, "approved");
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
}