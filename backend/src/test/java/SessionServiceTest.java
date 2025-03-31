
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.demo.Model.Session;
import com.example.demo.Repository.SessionRepository;
import com.example.demo.Service.SessionService;

class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Tests for getAllSession ---

    @Test
    void testGetAllSessionSuccess() {
        // Arrange
        Session session1 = new Session();
        session1.setBugId(1L);
        Session session2 = new Session();
        session2.setBugId(2L);
        List<Session> expectedSessions = Arrays.asList(session1, session2);

        when(sessionRepository.findAll()).thenReturn(expectedSessions);

        // Act
        List<Session> result = sessionService.getAllSession();

        // Assert: Group all checks into one compound assertion.
        assertAll("Get all session success",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.size(), "Result size should be 2"),
                () -> assertEquals(expectedSessions, result, "Returned sessions should match expected sessions")
        );
        verify(sessionRepository, times(1)).findAll();
    }


    @Test
    void testGetAllSessionEmpty() {
        // Arrange
        when(sessionRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Session> result = sessionService.getAllSession();

        // Assert: Group assertions into one compound assertion.
        assertAll("Empty session list assertions",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.isEmpty(), "Expected session list to be empty")
        );
        verify(sessionRepository, times(1)).findAll();
    }

    // --- Tests for getSessionByBugId ---

    @Test
    void testGetSessionByBugIdFound() {
        // Arrange
        Long bugId = 1L;
        Session session = new Session();
        session.setBugId(bugId);

        when(sessionRepository.findByBugId(bugId)).thenReturn(session);

        // Act
        Optional<Session> result = sessionService.getSessionByBugId(bugId);

        // Assert: Group both checks into one compound assertion.
        assertAll("Verify session found by bugId",
                () -> assertTrue(result.isPresent(), "Session should be present"),
                () -> assertEquals(session, result.get(), "Returned session should match expected")
        );
        verify(sessionRepository, times(1)).findByBugId(bugId);
    }


    @Test
    void testGetSessionByBugIdNotFound() {
        // Arrange
        Long bugId = 1L;
        when(sessionRepository.findByBugId(bugId)).thenReturn(null);

        // Act
        Optional<Session> result = sessionService.getSessionByBugId(bugId);

        // Assert
        assertFalse(result.isPresent());
        verify(sessionRepository, times(1)).findByBugId(bugId);
    }

    // --- Tests for getSessionById ---

    @Test
    void testGetSessionByIdFound() {
        // Arrange
        Long sessionId = 1L;
        Session session = new Session();
        session.setBugId(sessionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act
        Optional<Session> result = sessionService.getSessionById(sessionId);

        // Assert: Group assertions into a single compound assertion.
        assertAll("Verify session is found",
                () -> assertTrue(result.isPresent(), "Session should be present"),
                () -> assertEquals(session, result.get(), "Returned session should match expected session")
        );
        verify(sessionRepository, times(1)).findById(sessionId);
    }


    @Test
    void testGetSessionByIdNotFound() {
        // Arrange
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act
        Optional<Session> result = sessionService.getSessionById(sessionId);

        // Assert
        assertFalse(result.isPresent());
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    // --- Tests for createSession ---

    @Test
    void testCreateSessionNewSession() {
        // Arrange
        Session session = new Session();
        session.setOwnerId(1L);
        session.setBugId(1L);

        when(sessionRepository.existsByOwnerIdAndBugId(1L, 1L)).thenReturn(false);
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // Act
        Session result = sessionService.createSession(session);

        // Assert: Group both assertions into one compound assertion.
        assertAll("Session creation assertions",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(session, result, "Returned session should match the input session")
        );

        verify(sessionRepository, times(1)).existsByOwnerIdAndBugId(1L, 1L);
        verify(sessionRepository, times(1)).save(session);
        verify(sessionRepository, never()).findByOwnerIdAndBugId(anyLong(), anyLong());
    }


    @Test
    void testCreateSessionExistingSession() {
        // Arrange
        Session session = new Session();
        session.setOwnerId(1L);
        session.setBugId(1L);

        Session existingSession = new Session();
        existingSession.setOwnerId(1L);
        existingSession.setBugId(1L);

        when(sessionRepository.existsByOwnerIdAndBugId(1L, 1L)).thenReturn(true);
        when(sessionRepository.findByOwnerIdAndBugId(1L, 1L)).thenReturn(existingSession);

        // Act
        Session result = sessionService.createSession(session);

        // Assert: Group assertions into a single compound assertion.
        assertAll("Existing session returned",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(existingSession, result, "Returned session should match the existing session")
        );

        verify(sessionRepository, times(1)).existsByOwnerIdAndBugId(1L, 1L);
        verify(sessionRepository, times(1)).findByOwnerIdAndBugId(1L, 1L);
        verify(sessionRepository, never()).save(any(Session.class));
    }






    @Test
    void testGetAllSession() {
        // Given
        Session session1 = new Session();
        Session session2 = new Session();
        List<Session> expectedSessions = Arrays.asList(session1, session2);
        when(sessionRepository.findAll()).thenReturn(expectedSessions);

        // When
        List<Session> sessions = sessionService.getAllSession();

        // Then
        assertEquals(expectedSessions, sessions);
        verify(sessionRepository).findAll();
    }



    @Test
    void testCreateSessionWhenSessionExists() {
        // Given
        Session session = new Session();
        session.setOwnerId(10L);
        session.setBugId(20L);
        // Simulate an existing session for given owner and bug.
        when(sessionRepository.existsByOwnerIdAndBugId(session.getOwnerId(), session.getBugId())).thenReturn(true);
        when(sessionRepository.findByOwnerIdAndBugId(session.getOwnerId(), session.getBugId())).thenReturn(session);

        // When
        Session result = sessionService.createSession(session);

        // Then
        assertEquals(session, result);
        verify(sessionRepository).existsByOwnerIdAndBugId(session.getOwnerId(), session.getBugId());
        verify(sessionRepository).findByOwnerIdAndBugId(session.getOwnerId(), session.getBugId());
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void testCreateSessionWhenSessionDoesNotExist() {
        // Given
        Session session = new Session();
        session.setOwnerId(10L);
        session.setBugId(20L);
        // Simulate that no session exists for given owner and bug.
        when(sessionRepository.existsByOwnerIdAndBugId(session.getOwnerId(), session.getBugId())).thenReturn(false);
        when(sessionRepository.save(session)).thenReturn(session);

        // When
        Session result = sessionService.createSession(session);

        // Then
        assertEquals(session, result);
        verify(sessionRepository).existsByOwnerIdAndBugId(session.getOwnerId(), session.getBugId());
        verify(sessionRepository).save(session);
        verify(sessionRepository, never()).findByOwnerIdAndBugId(anyLong(), anyLong());
    }
}
