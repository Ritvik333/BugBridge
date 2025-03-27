
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.demo.Model.Comment;
import com.example.demo.Repository.CommentRepository;
import com.example.demo.Service.CommentService;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Tests for getCommentsByBugId ---

    @Test
    void testGetCommentsByBugIdSuccess() {
        // Arrange
        Long bugId = 1L;
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setBugId(bugId);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setBugId(bugId);
        List<Comment> expectedComments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByBugId(bugId)).thenReturn(expectedComments);

        // Act
        List<Comment> result = commentService.getCommentsByBugId(bugId);

        // Assert: Group all assertions into a single assertAll block.
        assertAll("Verify comments by bugId",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.size(), "Result list size should be 2"),
                () -> assertEquals(expectedComments, result, "Returned list should match expected comments")
        );
        verify(commentRepository, times(1)).findByBugId(bugId);
    }


    @Test
    void testGetCommentsByBugIdNoResults() {
        // Arrange
        Long bugId = 1L;
        when(commentRepository.findByBugId(bugId)).thenReturn(Collections.emptyList());

        // Act
        List<Comment> result = commentService.getCommentsByBugId(bugId);

        // Assert: Group assertions into one compound assertion.
        assertAll("Verify no comments returned",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.isEmpty(), "Expected result list to be empty")
        );
        verify(commentRepository, times(1)).findByBugId(bugId);
    }


    // --- Tests for createComment ---

    @Test
    void testCreateCommentSuccess() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setBugId(1L);
        comment.setText("Test comment");

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        Comment result = commentService.createComment(comment);

        // Assert: Group assertions into a single compound assertion.
        assertAll("Create Comment Success",
                () -> assertNotNull(result, "Saved comment should not be null"),
                () -> assertEquals(comment, result, "Saved comment should match the input comment")
        );

        verify(commentRepository, times(1)).save(comment);
    }

    // --- Tests for deleteCommentById ---

    @Test
    void testDeleteCommentByIdSuccess() {
        // Arrange
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(commentId);

        // Act
        boolean result = commentService.deleteCommentById(commentId);

        // Assert
        assertTrue(result);
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testDeleteCommentByIdNotFound() {
        // Arrange
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        // Act
        boolean result = commentService.deleteCommentById(commentId);

        // Assert
        assertFalse(result);
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, never()).deleteById(commentId);
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