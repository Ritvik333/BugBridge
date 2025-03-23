import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.example.demo.Model.Comment;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCommentsByBugId() {
        Long bugId = 1L;
        Comment comment1 = new Comment();
        comment1.setId(100L);
        Comment comment2 = new Comment();
        comment2.setId(101L);
        List<Comment> expectedComments = Arrays.asList(comment1, comment2);

        // When repository.findByBugId is called, return the expected list
        when(commentRepository.findByBugId(bugId)).thenReturn(expectedComments);

        List<Comment> actualComments = commentService.getCommentsByBugId(bugId);
        assertEquals(expectedComments, actualComments);
        verify(commentRepository).findByBugId(bugId);
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        comment.setId(200L);
        // When repository.save is called, return the same comment object
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment savedComment = commentService.createComment(comment);
        assertNotNull(savedComment);
        assertEquals(comment.getId(), savedComment.getId());
        verify(commentRepository).save(comment);
    }

    @Test
    void testDeleteCommentByIdWhenExists() {
        Long commentId = 300L;
        // Simulate that the comment exists
        when(commentRepository.existsById(commentId)).thenReturn(true);

        boolean result = commentService.deleteCommentById(commentId);
        assertTrue(result);
        // Verify that deleteById was called
        verify(commentRepository).existsById(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testDeleteCommentByIdWhenNotExists() {
        Long commentId = 400L;
        // Simulate that the comment does not exist
        when(commentRepository.existsById(commentId)).thenReturn(false);

        boolean result = commentService.deleteCommentById(commentId);
        assertFalse(result);
        // Verify that deleteById was never called
        verify(commentRepository).existsById(commentId);
        verify(commentRepository, never()).deleteById(commentId);
    }
}
