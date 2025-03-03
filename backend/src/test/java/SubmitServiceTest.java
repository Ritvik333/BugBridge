

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        when(submitRepository.findByUserIdAndBugId(userId, bugId)).thenReturn(Arrays.asList());

        // Act
        List<Submit> result = submitService.getSubmissionsForUserAndBug(userId, bugId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(submitRepository, times(1)).findByUserIdAndBugId(userId, bugId);
    }
}
