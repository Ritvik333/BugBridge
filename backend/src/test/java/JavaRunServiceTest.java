

import com.example.demo.Service.JavaRunService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JavaRunServiceTest {

    private JavaRunService javaRunService;

    @BeforeEach
    void setUp() {
        javaRunService = new JavaRunService();
    }

    @Test
    void testExecuteCode_SuccessfulExecution() throws Exception {
        // Arrange
        String javaCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        // Act
        String result = javaRunService.executeCode(javaCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Hello, World!"));
    }

    @Test
    void testExecuteCode_CompilationError() throws Exception {
        // Arrange
        String invalidJavaCode = "public class Main { public static void main(String[] args) { System.out.println( } }"; // Missing closing quote and parenthesis

        // Act
        String result = javaRunService.executeCode(invalidJavaCode);

        // Assert
        assertTrue(result.startsWith("Compilation Error:"));
        assertTrue(result.contains("error"));
    }

    @Test
    void testExecuteCode_Timeout() throws Exception {
        // Arrange
        String infiniteLoopCode = "public class Main { public static void main(String[] args) { while(true) {} } }";

        // Act
        String result = javaRunService.executeCode(infiniteLoopCode);

        // Assert
        assertEquals("Execution timed out (Limit: 5s)", result);
    }

    @Test
    void testExecuteCode_TempDirCleanup(@TempDir Path tempDir) throws Exception {
        // Arrange
        String javaCode = "public class Main { public static void main(String[] args) { System.out.println(\"Test\"); } }";

        // Act
        javaRunService.executeCode(javaCode);

        // Assert - Check that no temporary directories remain
        File[] tempDirs = tempDir.toFile().listFiles((dir, name) -> name.startsWith("java_temp_"));
        assertTrue(tempDirs == null || tempDirs.length == 0);
    }

    @Test
    void testGetLanguage() {
        // Act
        String language = javaRunService.getLanguage();

        // Assert
        assertEquals("java", language);
    }

    @Test
    void testExecuteCode_EmptyCode() throws Exception {
        // Arrange
        String emptyCode = "";

        // Act
        String result = javaRunService.executeCode(emptyCode);

        // Assert
        assertTrue(result.contains("Could not find or load main class Main"),
                "Expected runtime error for empty code, but got: '" + result + "'");
    }

    @Test
    void testExecuteCode_NullCode() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> javaRunService.executeCode(null));
    }
}