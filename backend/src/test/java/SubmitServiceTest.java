

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.demo.Service.SubmitService;

class SubmitServiceTest {

    @Mock
    private SubmitRepository submitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BugRepository bugRepository;

    @InjectMocks  // This will automatically inject the mocks into the submitService
    private SubmitService submitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes the mocks
    }

    @Test
    void testSaveSubmissionSuccess() throws IOException {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        User user = new User();  // Assume user object exists
        Bug bug = new Bug();  // Assume bug object exists

        // Mock repository calls
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(java.util.Optional.of(bug));

        // Mock submitRepository.save() to return the Submit object when called
        Submit mockSubmit = new Submit();
        when(submitRepository.save(any(Submit.class))).thenReturn(mockSubmit);

        // Act
        Submit submit = submitService.saveSubmission(userId, bugId, username, desc, code);

        // Assert
        assertNotNull(submit);  // Ensure the returned object is not null
        assertEquals(user, submit.getUser());  // Ensure the user is set correctly
        assertEquals(bug, submit.getBug());  // Ensure the bug is set correctly
        assertEquals(desc, submit.getDescription());  // Ensure the description is set correctly
        assertTrue(submit.getCodeFilePath().endsWith(".java"));  // Ensure file extension is correct (assuming Java)

        // Verify that save was called on the repository
        verify(submitRepository, times(1)).save(any(Submit.class));  // Ensure save is called once
    }


    @Test
    void testSaveSubmissionUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long bugId = 1L;
        String username = "john_doe";
        String desc = "Fixing bug in the login system";
        String code = "public class Main {}";

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty()); // Simulate user not found

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

        User user = new User();  // Assume user object exists
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(bugRepository.findById(bugId)).thenReturn(java.util.Optional.empty()); // Simulate bug not found

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            submitService.saveSubmission(userId, bugId, username, desc, code);
        });
        assertEquals("Bug not found", exception.getMessage());
    }

    @Test
    void testMapLanguageToExtension() {
        // Act & Assert
        assertEquals(".java", submitService.mapLanguageToExtension("java"));
        assertEquals(".py", submitService.mapLanguageToExtension("python"));
        assertEquals(".js", submitService.mapLanguageToExtension("javascript"));
        assertEquals(".txt", submitService.mapLanguageToExtension("other"));
        assertEquals(".txt", submitService.mapLanguageToExtension(null));
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