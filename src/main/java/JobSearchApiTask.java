import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;

public class JobSearchApiTask extends SearchTask {

    private static final int SEARCH_LIMIT = 100; // API limit: max 100 posts per sub search
    private static final int MAX_SEARCH_LIMIT = 2000; // API limit: max 2000 posts total per search
    public JobSearchApiTask(String search) {
        super(search);
    }

    @Override
    DataCollection requestResponse() {
        ArrayList<DataEntry> dataEntries;
        // API is limited to returning max 100 results per search, for bigger results more searches are needed.
        // Need to add (&offset=<value>) parameter to return a different part of search result.
        JsonNode jsonNode = executeSearch("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + SEARCH_LIMIT);
        System.out.println("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + SEARCH_LIMIT);
        int totalHits = jsonNode.path("total").get("value").asInt();
        dataEntries = new ArrayList<>(totalHits);
        // Store sub search
        if(totalHits < SEARCH_LIMIT) {
            dataEntries.addAll(savePosts(jsonNode, totalHits));
        } else {
            dataEntries.addAll(savePosts(jsonNode, SEARCH_LIMIT));
        }
        // Run sub searches when over 100+ hits
        int postsFetched = SEARCH_LIMIT;
        while(postsFetched < totalHits && postsFetched < MAX_SEARCH_LIMIT) {
            int postsLeft = totalHits - postsFetched;
            if(postsLeft > SEARCH_LIMIT) {
                JsonNode jNode = executeSearch("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + SEARCH_LIMIT + "&offset=" + postsFetched);
                postsFetched += SEARCH_LIMIT;
                dataEntries.addAll(savePosts(jNode, SEARCH_LIMIT));
            } else {
                JsonNode jNode = executeSearch("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + postsLeft + "&offset=" + postsFetched);
                postsFetched += postsLeft;
                dataEntries.addAll(savePosts(jNode, postsLeft));
            }
        }
        return new DataCollection(originalSearch, dataEntries);
    }

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
    private ArrayList<DataEntry> savePosts(JsonNode jsonNode, int size) {
        ArrayList<DataEntry> dataEntries = new ArrayList<>(size);
        Iterator<JsonNode> hits = jsonNode.path("hits").elements();
        while (hits.hasNext()) {
            JsonNode node = hits.next();
            System.out.println(node.get("headline"));
            dataEntries.add(new DataEntry(
                    node.get("id").asInt(),
                    node.get("headline").toString(),
                    node.get("description").get("text").toString()));
        }
        return dataEntries;
    }
}