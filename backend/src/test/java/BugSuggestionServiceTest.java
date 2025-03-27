import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.example.demo.Model.Bug;
import com.example.demo.Service.BugSuggestionService;
import com.example.demo.dto.SuggestedSolution;

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

    // Helper to create a service pre-configured with a JSON response.
    private TestBugSuggestionService createServiceWithResponse(String jsonResponse) {
        TestBugSuggestionService service = new TestBugSuggestionService();
        service.setResponseToReturn(jsonResponse);
        return service;
    }

    // --------------------------
    // Tests for successful API call (with valid bug values)
    // --------------------------

    @Test
    public void testGetSuggestedSolutions_success_listSize() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 100," +
                "\"body\": \"Test body content\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");
        bug.setDescription("Test description");

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertEquals(1, solutions.size(), "Expected one suggested solution");
    }

    @Test
    public void testGetSuggestedSolutions_success_title() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 100," +
                "\"body\": \"Test body content\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");
        bug.setDescription("Test description");

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("Test Title", sol.getTitle(), "Title should match");
    }

    @Test
    public void testGetSuggestedSolutions_success_link() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 100," +
                "\"body\": \"Test body content\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");
        bug.setDescription("Test description");

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("https://stackoverflow.com/questions/100", sol.getLink(), "Link should match");
    }

    @Test
    public void testGetSuggestedSolutions_success_summary() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 100," +
                "\"body\": \"Test body content\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");
        bug.setDescription("Test description");

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("Test body content...", sol.getSummary(), "Summary should be the truncated body with ellipsis");
    }

    // --------------------------
    // Tests for successful API call with null bug title and description
    // --------------------------

    @Test
    public void testGetSuggestedSolutions_nullValues_listSize() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 200," +
                "\"body\": \"Body text for null bug\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle(null);
        bug.setDescription(null);

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertEquals(1, solutions.size(), "Expected one suggested solution even if bug fields are null");
    }

    @Test
    public void testGetSuggestedSolutions_nullValues_title() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 200," +
                "\"body\": \"Body text for null bug\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle(null);
        bug.setDescription(null);

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("Test Title", sol.getTitle(), "Title should match");
    }

    @Test
    public void testGetSuggestedSolutions_nullValues_link() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 200," +
                "\"body\": \"Body text for null bug\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle(null);
        bug.setDescription(null);

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("https://stackoverflow.com/questions/200", sol.getLink(), "Link should match");
    }

    @Test
    public void testGetSuggestedSolutions_nullValues_summary() {
        String jsonResponse = "{" +
                "\"items\": [{" +
                "\"title\": \"Test Title\"," +
                "\"question_id\": 200," +
                "\"body\": \"Body text for null bug\"" +
                "}]," +
                "\"has_more\": false," +
                "\"backoff\": 0" +
                "}";
        TestBugSuggestionService service = createServiceWithResponse(jsonResponse);

        Bug bug = new Bug();
        bug.setTitle(null);
        bug.setDescription(null);

        SuggestedSolution sol = service.getSuggestedSolutions(bug).get(0);
        assertEquals("Body text for null bug...", sol.getSummary(), "Summary should match");
    }

    // --------------------------
    // Test for handling exception scenario
    // --------------------------

    @Test
    public void testGetSuggestedSolutions_exception() {
        TestBugSuggestionService service = new TestBugSuggestionService();
        service.setThrowException(true);

        Bug bug = new Bug();
        bug.setTitle("Test Bug");

        List<SuggestedSolution> solutions = service.getSuggestedSolutions(bug);
        assertTrue(solutions.isEmpty(), "Expected an empty list when an exception occurs");
    }
}
