import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.Model.Session;
import com.example.demo.Repository.SessionRepository;
import com.example.demo.Service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void testGetSessionByBugIdFound() {
        // Given
        Long bugId = 1L;
        Session session = new Session();
        session.setBugId(bugId);
        when(sessionRepository.findByBugId(bugId)).thenReturn(session);

        // When
        Optional<Session> result = sessionService.getSessionByBugId(bugId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(session, result.get());
        verify(sessionRepository).findByBugId(bugId);
    }

    @Test
    void testGetSessionByBugIdNotFound() {
        // Given
        Long bugId = 2L;
        when(sessionRepository.findByBugId(bugId)).thenReturn(null);

        // When
        Optional<Session> result = sessionService.getSessionByBugId(bugId);

        // Then
        assertFalse(result.isPresent());
        verify(sessionRepository).findByBugId(bugId);
    }

    @Test
    void testGetSessionByIdFound() {
        // Given
        Long sessionId = 1L;
        Session session = new Session();
        session.setSessionId(String.valueOf(sessionId));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // When
        Optional<Session> result = sessionService.getSessionById(sessionId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(session, result.get());
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    void testGetSessionByIdNotFound() {
        // Given
        Long sessionId = 2L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When
        Optional<Session> result = sessionService.getSessionById(sessionId);

        // Then
        assertFalse(result.isPresent());
        verify(sessionRepository).findById(sessionId);
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
