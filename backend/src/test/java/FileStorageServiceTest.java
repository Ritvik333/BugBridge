import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.FileStorageService;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    private Path mockStoragePath;

    @BeforeEach
    void setUp() throws Exception {
        // Set up the temporary storage path.
        mockStoragePath = tempDir.resolve("uploads");
        Files.createDirectories(mockStoragePath);
        // Instantiate FileStorageService with the temporary storage path.
        fileStorageService = new FileStorageService(mockStoragePath.toString());
    }

    // --- Tests for saveFile ---

    @Test
    void testSaveFileSuccess() throws IOException {
        // Arrange
        Long userId = 1L;
        String username = "john_doe";
        String language = "java";
        String filename = "TestFile.java";
        byte[] fileContent = "public class Test {}".getBytes();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getBytes()).thenReturn(fileContent);

        // Act
        String result = fileStorageService.saveFile(mockFile, userId, username, language);

        // Compute the expected absolute path based on mockStoragePath.
        Path expectedFilePath = mockStoragePath.resolve(userId + "_" + username).resolve(language).resolve(filename);
        String expectedPathString = expectedFilePath.toString();

        // Assert: use assertAll to group assertions.
        assertAll("File save assertions",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(expectedPathString, result, "Relative path should match expected absolute path")
                // Uncomment these if you wish to check file system effects:
                // () -> assertTrue(Files.exists(expectedFilePath), "Expected file to exist at the absolute path"),
                // () -> assertArrayEquals(fileContent, Files.readAllBytes(expectedFilePath), "File content should match")
        );

        verify(mockFile, times(1)).getOriginalFilename();
        verify(mockFile, times(1)).getBytes();
    }

    @Test
    void testSaveFileNullFilename() throws IOException {
        // Arrange
        Long userId = 1L;
        String username = "john_doe";
        String language = "java";

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(null); // Null filename

        // Act
        IOException exception = assertThrows(IOException.class, () ->
                fileStorageService.saveFile(mockFile, userId, username, language));

        // Assert: Group all checks into one assertAll block.
        assertAll("Verify invalid file name handling",
                () -> assertEquals("Invalid file name", exception.getMessage()),
                () -> verify(mockFile, times(1)).getOriginalFilename(),
                () -> verify(mockFile, never()).getBytes()
        );
    }

    @Test
    void testSaveFileEmptyFilename() throws IOException {
        // Arrange
        Long userId = 1L;
        String username = "john_doe";
        String language = "java";

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(""); // Empty filename

        // Act & Assert: Combine exception message check and verifications.
        IOException exception = assertThrows(IOException.class, () ->
                fileStorageService.saveFile(mockFile, userId, username, language));
        assertAll("Verify invalid file handling",
                () -> assertEquals("Invalid file name", exception.getMessage()),
                () -> verify(mockFile, times(1)).getOriginalFilename(),
                () -> verify(mockFile, never()).getBytes()
        );
    }

    @Test
    void testSaveFileCreatesDirectories() throws IOException {
        // Arrange
        Long userId = 1L;
        String username = "john_doe";
        String language = "python";
        String filename = "script.py";
        byte[] fileContent = "print('Hello')".getBytes();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(filename);
        when(mockFile.getBytes()).thenReturn(fileContent);

        Path expectedDirectory = mockStoragePath.resolve(userId + "_" + username).resolve(language);
        // Precondition: Directory doesn't exist yet.
        assertFalse(Files.exists(expectedDirectory), "Expected directory not to exist before save");

        // Act
        String result = fileStorageService.saveFile(mockFile, userId, username, language);

        // Compute expected values.
        String expectedRelativePath = mockStoragePath.resolve(userId + "_" + username).resolve(language).resolve(filename).toString();
        Path absolutePath = expectedDirectory.resolve(filename);

        // Assert: Group all outcome checks.
        assertAll("File save assertions",
                () -> assertNotNull(result, "Resulting file path should not be null"),
                () -> assertEquals(expectedRelativePath, result, "Relative path should match"),
                () -> assertTrue(Files.exists(expectedDirectory), "Expected directory to be created"),
                () -> assertTrue(Files.exists(absolutePath), "Expected file to exist at the absolute path"),
                () -> assertArrayEquals(fileContent, Files.readAllBytes(absolutePath), "File content should match")
        );

        verify(mockFile, times(1)).getOriginalFilename();
        verify(mockFile, times(1)).getBytes();
    }
}
