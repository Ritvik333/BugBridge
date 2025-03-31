
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.Service.RunService;
import com.example.demo.Service.RunServiceFactory;

public class RunServiceFactoryTest {

    private RunServiceFactory runServiceFactory;
    private RunService mockPythonService;
    private RunService mockJavaService;
    private RunService mockJavaScriptService;

    @BeforeEach
    void setUp() {
        // Mock RunService implementations
        mockPythonService = mock(RunService.class);
        mockJavaService = mock(RunService.class);
        mockJavaScriptService = mock(RunService.class);

        // Configure mock behavior
        when(mockPythonService.getLanguage()).thenReturn("python");
        when(mockJavaService.getLanguage()).thenReturn("java");
        when(mockJavaScriptService.getLanguage()).thenReturn("javascript");

        // Initialize the factory with the mocked services
        List<RunService> services = Arrays.asList(mockPythonService, mockJavaService, mockJavaScriptService);
        runServiceFactory = new RunServiceFactory(services);
    }

    @Test
    void testConstructor_PopulatesRunServicesMap() {
        // Act & Assert
        assertEquals(mockPythonService, runServiceFactory.getRunService("python"));
        assertEquals(mockJavaService, runServiceFactory.getRunService("java"));
        assertEquals(mockJavaScriptService, runServiceFactory.getRunService("javascript"));
        verify(mockPythonService).getLanguage();
        verify(mockJavaService).getLanguage();
        verify(mockJavaScriptService).getLanguage();
    }

    @Test
    void testGetRunService_CaseInsensitive() {
        // Act & Assert
        assertEquals(mockPythonService, runServiceFactory.getRunService("PYTHON"));
        assertEquals(mockJavaService, runServiceFactory.getRunService("Java"));
        assertEquals(mockJavaScriptService, runServiceFactory.getRunService("JavaScript"));
    }

    @Test
    void testGetRunService_UnsupportedLanguage() {
        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> runServiceFactory.getRunService("ruby")
        );
        assertEquals("Unsupported language: ruby", exception.getMessage());
    }

    @Test
    void testGetRunService_NullLanguage() {
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> runServiceFactory.getRunService(null)
        );
        assertTrue(exception.getMessage().contains("null"), "Expected NPE for null language");
    }

    @Test
    void testConstructor_EmptyServiceList() {
        // Arrange
        List<RunService> emptyServices = Arrays.asList();
        RunServiceFactory emptyFactory = new RunServiceFactory(emptyServices);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> emptyFactory.getRunService("python")
        );
        assertEquals("Unsupported language: python", exception.getMessage());
    }

    @Test
    void testConstructor_DuplicateLanguages() {
        // Arrange
        RunService duplicatePythonService = mock(RunService.class);
        when(duplicatePythonService.getLanguage()).thenReturn("python");
        List<RunService> servicesWithDuplicate = Arrays.asList(mockPythonService, duplicatePythonService);
        RunServiceFactory factoryWithDuplicate = new RunServiceFactory(servicesWithDuplicate);

        // Act
        RunService result = factoryWithDuplicate.getRunService("python");

        // Assert - Last service should override previous ones
        assertEquals(duplicatePythonService, result);
    }
}