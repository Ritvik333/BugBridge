import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.example.demo.Model.Bug;
import com.example.demo.Service.BugSuggestionService;
import com.example.demo.dto.SuggestedSolution;

// Test subclass to override the external API call.
public class BugSuggestionServiceTest {

    // Extend the service to override executeCurlCommand.
    private static class TestBugSuggestionService extends BugSuggestionService {
        private String responseToReturn;
        private boolean throwException;

        public void setResponseToReturn(String responseToReturn) {
            this.responseToReturn = responseToReturn;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        protected String executeCurlCommand(String url) throws IOException, InterruptedException {
            if (throwException) {
                throw new IOException("Simulated exception");
            }
            return responseToReturn;
        }
    }

    // Test a successful API call returning one suggested solution.
    @Test
    public void testGetSuggestedSolutions_success() {
        TestBugSuggestionService service = new TestBugSuggestionService();
        // Create a JSON response with one item.
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 100," +
                "\"body\": \"Test body content\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        service.setResponseToReturn(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");
        bug.setDescription("Test description");

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertEquals(1, solutions.size(), "Expected one suggested solution");
        SuggestedSolution sol = solutions.get(0);
        assertAll("Solution properties",
                () -> assertEquals("Test Title", sol.getTitle(), "Title should match"),
                () -> assertEquals("https://stackoverflow.com/questions/100", sol.getLink(), "Link should match"),
                () -> assertEquals("Test body content...", sol.getSummary(), "Summary should be the truncated body with ellipsis")
        );
    }

    // Test that when executeCurlCommand throws an exception, the service returns an empty list.
    @Test
    public void testGetSuggestedSolutions_exception() {
        TestBugSuggestionService service = new TestBugSuggestionService();
        service.setThrowException(true);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertTrue(solutions.isEmpty(), "Expected an empty list when an exception occurs");
    }

    // Test that null bug title and description are handled properly.
    @Test
    public void testGetSuggestedSolutions_nullValues() {
        TestBugSuggestionService service = new TestBugSuggestionService();
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 200," +
                "\"body\": \"Body text for null bug\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        service.setResponseToReturn(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle(null);
        bug.setDescription(null);

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertEquals(1, solutions.size(), "Expected one suggested solution even if title/description are null");
        SuggestedSolution sol = solutions.get(0);
        assertAll("Solution properties with null bug fields",
                () -> assertEquals("Test Title", sol.getTitle(), "Title should match"),
                () -> assertEquals("https://stackoverflow.com/questions/200", sol.getLink(), "Link should match"),
                () -> assertEquals("Body text for null bug...", sol.getSummary(), "Summary should match")
        );
    }
}
