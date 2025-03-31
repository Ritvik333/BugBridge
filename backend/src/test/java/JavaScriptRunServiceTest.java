
import com.example.demo.Service.JavaScriptRunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JavaScriptRunServiceTest {

    private JavaScriptRunService javaScriptRunService;

    @BeforeEach
    void setUp() {
        javaScriptRunService = new JavaScriptRunService();
    }

    @Test
    void testExecuteCode_SuccessfulExecution() throws Exception {
        // Arrange
        String jsCode = "console.log('Hello, World!');";

        // Act
        String result = javaScriptRunService.executeCode(jsCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Hello, World!"));
    }

    @Test
    void testExecuteCode_Timeout() throws Exception {
        // Arrange
        String infiniteLoopCode = "while(true) {}";

        // Act
        String result = javaScriptRunService.executeCode(infiniteLoopCode);

        // Assert
        assertEquals("Execution timed out (Limit: 5s)", result);
    }

    @Test
    void testExecuteCode_InvalidJavaScriptCode() throws Exception {
        // Arrange
        String invalidCode = "console.log('Hello';"; // Missing closing parenthesis

        // Act
        String result = javaScriptRunService.executeCode(invalidCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("SyntaxError") || result.contains("error"));
    }

    @Test
    void testExecuteCode_TempFileCleanup(@TempDir Path tempDir) throws Exception {
        // Arrange
        String jsCode = "console.log('Test');";

        // Act
        javaScriptRunService.executeCode(jsCode);

        // Assert - Check that no temporary files remain
        File[] tempFiles = tempDir.toFile().listFiles((dir, name) -> name.startsWith("script") && name.endsWith(".js"));
        assertTrue(tempFiles == null || tempFiles.length == 0);
    }



    @Test
    void testGetLanguage() {
        // Act
        String language = javaScriptRunService.getLanguage();

        // Assert
        assertEquals("javascript", language);
    }

    @Test
    void testExecuteCode_EmptyCode() throws Exception {
        // Arrange
        String emptyCode = "";

        // Act
        String result = javaScriptRunService.executeCode(emptyCode);

        // Assert
        assertEquals("", result.trim(), "Expected empty output for empty code, but got: '" + result + "'");
    }

    @Test
    void testExecuteCode_NullCode() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> javaScriptRunService.executeCode(null));
    }

}