import com.example.demo.Model.Bug;
import com.example.demo.Model.Submit;
import com.example.demo.Model.User;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Repository.SubmitRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRejectSubmission_ValidCase() {
        // Arrange
        Long submissionId = 1L;
        Long rejecterId = 102L;

        User creator = new User();
        creator.setId(rejecterId);

        Bug bug = new Bug();
        bug.setId(1L);
        bug.setCreator(creator);

        User user = new User();
        user.setId(302L);

        Submit submission = new Submit();
        submission.setId(submissionId);
        submission.setBug(bug);
        submission.setUser(user);
        submission.setApprovalStatus("unapproved");

        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(submitRepository.save(any(Submit.class))).thenReturn(submission);

        // Act
        String result = submitService.rejectSubmission(submissionId, rejecterId);

        // Assert
        assertEquals("Submission rejected successfully.", result);
        assertEquals("rejected", submission.getApprovalStatus());
        verify(submitRepository, times(1)).save(submission);
        verify(notificationService, times(1)).createNotification(user.getId(),
                "Your submission for bug #" + bug.getId() + " has been rejected.");
    }

    @Test
    void testRejectSubmission_NullSubmissionId() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> submitService.rejectSubmission(null, 102L));
    }

    @Test
    void testRejectSubmission_NullRejecterId() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> submitService.rejectSubmission(1L, null));
    }

    @Test
    void testRejectSubmission_SubmissionNotFound() {
        // Arrange
        Long submissionId = 1L;
        Long rejecterId = 102L;

        when(submitRepository.findById(submissionId)).thenReturn(Optional.empty());

        // Act
        String result = submitService.rejectSubmission(submissionId, rejecterId);

        // Assert
        assertEquals("Submission not found.", result);
        verify(submitRepository, never()).save(any(Submit.class));
        verify(notificationService, never()).createNotification(anyLong(), anyString());
    }

    @Test
    void testRejectSubmission_UserNotBugCreator() {
        // Arrange
        Long submissionId = 1L;
        Long rejecterId = 302L;

        User creator = new User();
        creator.setId(102L);

        Bug bug = new Bug();
        bug.setId(1L);
        bug.setCreator(creator);

        Submit submission = new Submit();
        submission.setId(submissionId);
        submission.setBug(bug);

        when(submitRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        // Act
        String result = submitService.rejectSubmission(submissionId, rejecterId);

        // Assert
        assertEquals("Only the bug creator can reject submissions.", result);
        verify(submitRepository, never()).save(any(Submit.class));
        verify(notificationService, never()).createNotification(anyLong(), anyString());
    }

    @Test
    void testGetUnapprovedSubmissions() {
        // Arrange
        Submit submission1 = new Submit();
        submission1.setApprovalStatus("unapproved");

        Submit submission2 = new Submit();
        submission2.setApprovalStatus("unapproved");

        List<Submit> unapprovedSubmissions = Arrays.asList(submission1, submission2);
        when(submitRepository.findByApprovalStatus("unapproved")).thenReturn(unapprovedSubmissions);

        // Act
        List<Submit> result = submitService.getUnapprovedSubmissions();

        // Assert
        assertEquals(2, result.size());
        verify(submitRepository, times(1)).findByApprovalStatus("unapproved");
    }

    @Test
    void testGetApprovedSubmissions() {
        // Arrange
        Submit submission1 = new Submit();
        submission1.setApprovalStatus("approved");

        Submit submission2 = new Submit();
        submission2.setApprovalStatus("approved");

        List<Submit> approvedSubmissions = Arrays.asList(submission1, submission2);
        when(submitRepository.findByApprovalStatus("approved")).thenReturn(approvedSubmissions);

        // Act
        List<Submit> result = submitService.getApprovedSubmissions();

        // Assert
        assertEquals(2, result.size());
        verify(submitRepository, times(1)).findByApprovalStatus("approved");
    }

    @Test
    void testGetSubmissionsForCreatedBugs_ValidCreatorId() {
        // Arrange
        Long creatorId = 102L;

        User creator = new User();
        creator.setId(creatorId);

        Bug bug1 = new Bug();
        bug1.setId(1L);
        bug1.setCreator(creator);

        Bug bug2 = new Bug();
        bug2.setId(2L);
        bug2.setCreator(creator);

        Submit submission1 = new Submit();
        submission1.setBug(bug1);

        Submit submission2 = new Submit();
        submission2.setBug(bug2);

        List<Bug> createdBugs = Arrays.asList(bug1, bug2);
        when(bugRepository.findByCreatorId(creatorId)).thenReturn(createdBugs);
        when(submitRepository.findByBugId(1L)).thenReturn(Collections.singletonList(submission1));
        when(submitRepository.findByBugId(2L)).thenReturn(Collections.singletonList(submission2));

        // Act
        List<Submit> result = submitService.getSubmissionsForCreatedBugs(creatorId);

        // Assert
        assertEquals(2, result.size());
        verify(bugRepository, times(1)).findByCreatorId(creatorId);
        verify(submitRepository, times(1)).findByBugId(1L);
        verify(submitRepository, times(1)).findByBugId(2L);
    }

    @Test
    void testGetSubmissionsForCreatedBugs_NoCreatedBugs() {
        // Arrange
        Long creatorId = 102L;

        when(bugRepository.findByCreatorId(creatorId)).thenReturn(Collections.emptyList());

        // Act
        List<Submit> result = submitService.getSubmissionsForCreatedBugs(creatorId);

        // Assert
        assertEquals(0, result.size());
        verify(bugRepository, times(1)).findByCreatorId(creatorId);
        verify(submitRepository, never()).findByBugId(anyLong());
    }
}
