import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class JobTechAPISearch extends Task<DataCollection> {

    protected static final int API_SUB_SEARCH_LIMIT = 100; // API limit: max 100 posts per sub search
    protected static final int API_SEARCH_LIMIT = 2000; // API limit: max 2000 posts total per search
    protected String source;    // Data source name
    protected String date;
    protected String originalSearch;
    protected String encodedSearch;
    protected int searchOffset = 0; // Search property
    protected int postLimit; // Search property
    protected String lastDate; // Search property


    protected JobTechAPISearch(String search) {
        this.originalSearch = search;
        try {
            this.encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            this.encodedSearch = "";
        }
    }

    @Override
    protected DataCollection call() {
        return requestResponse();
    }

    private DataCollection requestResponse() {
        ArrayList<DataCollectionEntry> dataEntries;
        // API is limited to returning max 100 results per search, for bigger results more searches are needed.
        // Need to add (&offset=<value>) parameter to return a different part of search result.
        JsonNode jsonNode = executeSearch(initialSearchString());
        System.out.println(initialSearchString()); // TODO: REMOVE DEBUG CODE
        int totalHits = jsonNode.path("total").get("value").asInt();
        final int progress = totalHits;
        System.out.println("TOTAL HITS = " + totalHits);

        dataEntries = new ArrayList<>(totalHits);
        int postsFetched = 0;
        // Store first sub search
        if (totalHits < API_SUB_SEARCH_LIMIT) {
            dataEntries.addAll(savePosts(jsonNode, totalHits));
            postsFetched += totalHits;
        } else {
            dataEntries.addAll(savePosts(jsonNode, API_SUB_SEARCH_LIMIT));
            postsFetched += API_SUB_SEARCH_LIMIT;
        }

        // If there are more posts to fetch
        searchOffset = postsFetched; // Search offset
        while (postsFetched < totalHits) {
            // Encode date to UTF8
            try {
                lastDate = URLEncoder.encode(lastDate, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            boolean lastSearch = postsFetched + API_SEARCH_LIMIT > totalHits;
            postLimit = API_SUB_SEARCH_LIMIT;
            // Get SEARCH_LIMIT number of posts before constructing new search
            while (searchOffset < API_SEARCH_LIMIT && postsFetched < totalHits) {
                // Get exact number of last posts in search.
                if (lastSearch && (totalHits - postsFetched) < API_SUB_SEARCH_LIMIT) {
                    postLimit = totalHits - postsFetched;
                }
                JsonNode jNode = executeSearch(subSearchString());
                postsFetched += postLimit;
                dataEntries.addAll(savePosts(jNode, postLimit));

                // TODO: Remove debug code
                System.out.println(subSearchString());

                searchOffset += postLimit;
                // Send updates to GUI
                updateProgress(postsFetched, progress);
                updateMessage(postsFetched + " / " + progress);
            }
            searchOffset = 0;
            // Get DateTime of last post to use when constructing new search query
            lastDate = dataEntries.get(dataEntries.size() - 1).date().toString();
            System.out.println("RUN AGAIN FROM LASTDATE = " + lastDate); // TODO: Remove debug code
        }
        return new DataCollection(originalSearch, dataEntries, source, date);
    }

    // Creating
    protected abstract String initialSearchString();

    protected abstract String subSearchString();

    private JsonNode executeSearch(String search) {
        String rawJson = httpGetRequest(search);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(rawJson);
        } catch (JsonProcessingException e) {
            System.out.println("Throw error!");
            throw new RuntimeException(e);
        }
        return jsonNode;
    }

    private String httpGetRequest(String request) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(request))
                .build();
        try {
            return client.send(getRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<DataCollectionEntry> savePosts(JsonNode jsonNode, int size) {
        ArrayList<DataCollectionEntry> dataEntries = new ArrayList<>(size);
        Iterator<JsonNode> hits = jsonNode.path("hits").elements();
        while (hits.hasNext()) {
            JsonNode node = hits.next();
            System.out.println(node.get("headline")); // TODO: Remove debug code
            System.out.println(node.get("workplace_address").get("region").toString());


            String sDate = node.get("publication_date").toString();
            sDate = sDate.substring(1, sDate.length() - 1); // Remove quotation marks in response
            LocalDateTime pubDate = LocalDateTime.parse(sDate);
            System.out.println(pubDate); // TODO: Remove debug code

            dataEntries.add(new DataCollectionEntry(
                    node.get("id").toString(),
                    node.get("headline").toString(),
                    node.get("description").get("text").toString(),
                    pubDate,
                    node.get("workplace_address").get("region").toString()));

        }
        return dataEntries;
    }
}
