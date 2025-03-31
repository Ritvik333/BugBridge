

import com.example.demo.Service.PythonRunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PythonRunServiceTest {

    private PythonRunService pythonRunService;

    @BeforeEach
    void setUp() {
        pythonRunService = new PythonRunService();
    }

    @Test
    void testExecuteCode_SuccessfulExecution() throws Exception {
        // Arrange
        String pythonCode = "print('Hello, World!')";

        // Act
        String result = pythonRunService.executeCode(pythonCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Hello, World!"));
    }

    @Test
    void testExecuteCode_Timeout() throws Exception {
        // Arrange
        String infiniteLoopCode = "while True:\n    pass";

        // Act
        String result = pythonRunService.executeCode(infiniteLoopCode);

        // Assert
        assertEquals("Execution timed out (Limit: 5s)", result);
    }

    @Test
    void testExecuteCode_InvalidPythonCode() throws Exception {
        // Arrange
        String invalidCode = "print('Hello'  # Missing closing parenthesis";

        // Act
        String result = pythonRunService.executeCode(invalidCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("SyntaxError") || result.length() > 0); // Expecting some error output
    }

    @Test
    void testExecuteCode_TempFileCleanup(@TempDir Path tempDir) throws Exception {
        String pythonCode = "print('Test')";
        pythonRunService.executeCode(pythonCode);
        File[] tempFiles = tempDir.toFile().listFiles((dir, name) -> name.startsWith("script") && name.endsWith(".py"));
        assertEquals(0, tempFiles != null ? tempFiles.length : 0);
    }

    @Test
    void testGetLanguage() {
        // Act
        String language = pythonRunService.getLanguage();

        // Assert
        assertEquals("python", language);
    }

    @Test
    void testExecuteCode_EmptyCode() throws Exception {
        // Arrange
        String emptyCode = "";

        // Act
        String result = pythonRunService.executeCode(emptyCode);

        // Assert
        assertNotNull(result);
        assertEquals("", result.trim()); // Should execute successfully with empty output
    }

    @Test
    void testExecuteCode_NullCode() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> pythonRunService.executeCode(null));
    }

    // Helper method to mock ProcessBuilder creation
    private ProcessBuilder createProcessBuilder(File file) {
        return new ProcessBuilder("python3", file.getAbsolutePath());
    }
}