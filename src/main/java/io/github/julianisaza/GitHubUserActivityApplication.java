package io.github.julianisaza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class GitHubUserActivityApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GitHubUserActivityApplication.class);

    private static final String PAYLOAD = "payload";

    public static void main(String[] args) {
        SpringApplication.run(GitHubUserActivityApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length != 1) {
            logger.error("Usage: java -jar app.jar <username>");
            return;
        }

        String username = args[0];
        fetchAndDisplayActivity(username);
    }

    private void fetchAndDisplayActivity(String username) {
        try {
            HttpResponse<String> response = sendRequest(username);
            if (response.statusCode() == 200) {
                processSuccessfulResponse(response);
            } else {
                handleErrorResponse(response.statusCode(), username);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Request interrupted: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Network error: {}", e.getMessage());
        }
    }

    private HttpResponse<String> sendRequest(String username) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/users/" + username + "/events"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void processSuccessfulResponse(HttpResponse<String> response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        int count = 0;
        for (JsonNode event : root) {
            if (count < 10 && event.get("type") != null && event.get("repo") != null && event.get("repo").get("name") != null) {
                String type = event.get("type").asText();
                String repo = event.get("repo").get("name").asText();

                String message = formatEvent(type, event, repo);
                if (message != null) {
                    logger.info(message);
                    count++;
                }
            }
        }
    }

    private void handleErrorResponse(int statusCode, String username) {
        switch (statusCode) {
            case 404 -> logger.error("User not found: {}", username);
            case 403 -> logger.error("API rate limit exceeded. Try again later.");
            default -> logger.error("Error fetching data: HTTP {}", statusCode);
        }
    }

    private String formatEvent(String type, JsonNode event, String repo) {
        try {
            switch (type) {
                case "PushEvent":
                    return formatPushEvent(event, repo);
                case "IssuesEvent":
                    return formatIssuesEvent(event, repo);
                case "WatchEvent":
                    return "Starred " + repo;
                case "ForkEvent":
                    return "Forked " + repo;
                case "CreateEvent":
                    JsonNode payload = event.get(PAYLOAD);
                    if (payload != null) {
                        String refType = payload.get("ref_type").asText();
                        if ("repository".equals(refType)) {
                            return "Created repository " + repo;
                        } else {
                            String ref = payload.get("ref").asText();
                            return "Created " + refType + " " + ref + " in " + repo;
                        }
                    }
                    return null;
                default:
                    return null;
                // Add more cases as needed
            }
        } catch (NullPointerException e) {
            logger.debug("Skipping event due to missing fields: {}", e.getMessage());
        } catch (RuntimeException e) {
            logger.debug("Skipping malformed event: {}", e.getMessage());
        }
        return null; // Skip unknown or malformed events
    }

    private String formatPushEvent(JsonNode event, String repo) {
        JsonNode commitsNode = event.get(PAYLOAD).get("commits");
        if (commitsNode != null && commitsNode.isArray()) {
            int commits = commitsNode.size();
            return "Pushed " + commits + " commit" + (commits != 1 ? "s" : "") + " to " + repo;
        }
        return null;
    }

    private String formatIssuesEvent(JsonNode event, String repo) {
        JsonNode actionNode = event.get(PAYLOAD).get("action");
        if (actionNode != null) {
            String action = actionNode.asText();
            if ("opened".equals(action)) {
                return "Opened a new issue in " + repo;
            } else if ("closed".equals(action)) {
                return "Closed an issue in " + repo;
            }
        }
        return null;
    }
}
