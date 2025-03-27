
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.demo.Model.Bug;
import com.example.demo.Repository.BugRepository;
import com.example.demo.Service.BugService;

class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @InjectMocks
    private BugService bugService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Tests for getBugs ---

    @Test
    void testGetBugsSuccess() {
        // Arrange
        Bug bug1 = new Bug();
        bug1.setId(1L);
        Bug bug2 = new Bug();
        bug2.setId(2L);
        List<Bug> expectedBugs = Arrays.asList(bug1, bug2);

        when(bugRepository.findAll()).thenReturn(expectedBugs);

        // Act
        List<Bug> result = bugService.getBugs(null, null, null, null, null);

        // Assert: Group all checks into one compound assertion.
        assertAll("Get bugs success validations",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.size(), "Expected 2 bugs in the result"),
                () -> assertEquals(expectedBugs, result, "Returned bugs should match the expected list")
        );

        verify(bugRepository, times(1)).findAll();
    }

    @Test
    void testGetBugsNoResults() {
        // Arrange
        when(bugRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Bug> result = bugService.getBugs(null, null, null, null, null);

        // Assert: Group assertions in one compound assertion.
        assertAll("Get bugs no results validations",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.isEmpty(), "Expected result list to be empty")
        );

        verify(bugRepository, times(1)).findAll();
    }

    // --- Tests for getBugById ---

    @Test
    void testGetBugByIdFound() {
        // Arrange
        Long bugId = 1L;
        Bug expectedBug = new Bug();
        expectedBug.setId(bugId);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(expectedBug));

        // Act
        Bug result = bugService.getBugById(bugId);

        // Assert: Group both assertions into one compound assertion.
        assertAll("Get bug by ID validations",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(expectedBug, result, "Returned bug should match the expected bug")
        );

        verify(bugRepository, times(1)).findById(bugId);
    }


    @Test
    void testGetBugByIdNotFound() {
        // Arrange
        Long bugId = 1L;
        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act
        Bug result = bugService.getBugById(bugId);

        // Assert
        assertNull(result);
        verify(bugRepository, times(1)).findById(bugId);
    }

    // --- Tests for createBug ---

    @Test
    void testCreateBugSuccess() {
        // Arrange
        Bug bug = new Bug();
        bug.setId(1L);
        bug.setSeverity("High");

        when(bugRepository.save(any(Bug.class))).thenReturn(bug);

        // Act
        Bug result = bugService.createBug(bug);

        // Assert: Group assertions in a single assertAll.
        assertAll("Create bug success validations",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(bug, result, "Returned bug should match the created bug")
        );

        verify(bugRepository, times(1)).save(bug);
    }


    // --- Tests for updateBug ---

    @Test
    void testUpdateBugSuccess() {
        // Arrange
        Long bugId = 1L;
        Bug updatedBug = new Bug();
        updatedBug.setId(bugId);
        updatedBug.setSeverity("Low");

        Bug existingBug = new Bug();
        existingBug.setId(bugId);
        existingBug.setSeverity("High");

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(existingBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(updatedBug);

        // Act
        Bug result = bugService.updateBug(updatedBug);

        // Assert: Group both assertions into one compound assertion.
        assertAll("Update bug success validations",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(updatedBug, result, "Returned bug should match the updated bug")
        );

        verify(bugRepository, times(1)).findById(bugId);
        verify(bugRepository, times(1)).save(updatedBug);
    }

    @Test
    void testUpdateBugNotFound() {
        // Arrange
        Long bugId = 1L;
        Bug updatedBug = new Bug();
        updatedBug.setId(bugId);

        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act
        Bug result = bugService.updateBug(updatedBug);

        // Assert
        assertNull(result);
        verify(bugRepository, times(1)).findById(bugId);
        verify(bugRepository, never()).save(any(Bug.class));
    }

    // --- Tests for deleteBug ---

    @Test
    void testDeleteBugSuccess() {
        // Arrange
        Long bugId = 1L;
        Bug existingBug = new Bug();
        existingBug.setId(bugId);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(existingBug));
        doNothing().when(bugRepository).deleteById(bugId);

        // Act
        boolean result = bugService.deleteBug(bugId);

        // Assert
        assertTrue(result);
        verify(bugRepository, times(1)).findById(bugId);
        verify(bugRepository, times(1)).deleteById(bugId);
    }

    @Test
    void testDeleteBugNotFound() {
        // Arrange
        Long bugId = 1L;
        when(bugRepository.findById(bugId)).thenReturn(Optional.empty());

        // Act
        boolean result = bugService.deleteBug(bugId);

        // Assert
        assertFalse(result);
        verify(bugRepository, times(1)).findById(bugId);
        verify(bugRepository, never()).deleteById(anyLong());
    }
}