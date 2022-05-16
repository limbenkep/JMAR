import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

public class JobSearchApiTask extends SearchTask {

    private static final int SUB_SEARCH_LIMIT = 100; // API limit: max 100 posts per sub search
    private static final int SEARCH_LIMIT = 2000; // API limit: max 2000 posts total per search
    public JobSearchApiTask(String search) {
        super(search);
    }


    @Override
    DataCollection requestResponse() {
        ArrayList<DataEntry> dataEntries;
        // API is limited to returning max 100 results per search, for bigger results more searches are needed.
        // Need to add (&offset=<value>) parameter to return a different part of search result.
        JsonNode jsonNode = executeSearch("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + SUB_SEARCH_LIMIT + "&sort=pubdate-desc");
        System.out.println("https://jobsearch.api.jobtechdev.se/search?q=" + encodedSearch + "&limit=" + SUB_SEARCH_LIMIT + "&sort=pubdate-desc");
        int totalHits = jsonNode.path("total").get("value").asInt();
        final int progress = totalHits;
        System.out.println("TOTAL HITS = " + totalHits);

        dataEntries = new ArrayList<>(totalHits);
        int postsFetched = 0;
        // Store first sub search
        if(totalHits < SUB_SEARCH_LIMIT) {
            dataEntries.addAll(savePosts(jsonNode, totalHits));
            postsFetched += totalHits;
        } else {
            dataEntries.addAll(savePosts(jsonNode, SUB_SEARCH_LIMIT));
            postsFetched += SUB_SEARCH_LIMIT;
        }
        // Get current time without milliseconds
        String lastDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME);
        // If there are more posts to fetch
        int searchOffset = postsFetched; // Search offset
        while(postsFetched < totalHits) {
            // Encode date to UTF8
            try {
                lastDate = URLEncoder.encode(lastDate, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            boolean lastSearch = postsFetched + SEARCH_LIMIT > totalHits;
            int numToGet = SUB_SEARCH_LIMIT;
            // Get SEARCH_LIMIT number of posts before constructing new search
            while(searchOffset < SEARCH_LIMIT && postsFetched < totalHits) {
                // Get exact number of last posts in search.
                if(lastSearch && (totalHits - postsFetched) < SUB_SEARCH_LIMIT) {
                    numToGet = totalHits - postsFetched;
                }
                JsonNode jNode = executeSearch("https://jobsearch.api.jobtechdev.se/search?published-before=" + lastDate
                                            + "&q=" + encodedSearch
                                            + "&limit=" + numToGet
                                            + "&offset=" + searchOffset
                                            + "&sort=pubdate-desc");
                postsFetched += numToGet;
                dataEntries.addAll(savePosts(jNode, numToGet));

                // TODO: Remove debug code
                //System.out.println("ADDED POSTS = " + numToGet + " = " + postsFetched + " (size = " + dataEntries.size() + ") offset = " + searchOffset);
                System.out.println("https://jobsearch.api.jobtechdev.se/search?published-before=" + lastDate
                                + "&q=" + encodedSearch
                                + "&limit=" + numToGet//SUB_SEARCH_LIMIT
                                + "&offset=" + searchOffset
                                + "&sort=pubdate-desc");

                searchOffset += numToGet;
                // Send updates to GUI
                updateProgress(postsFetched, progress);
                updateMessage( postsFetched + " / " + progress);
            }
            searchOffset = 0;
            // Get DateTime of last post to use when constructing new search query
            lastDate = dataEntries.get(dataEntries.size()-1).date().toString();
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
            System.out.println(node.get("headline")); // TODO: Remove debug code
            String sDate = node.get("publication_date").toString();
            sDate = sDate.substring(1, sDate.length() - 1); // Remove quotation marks in response
            LocalDateTime pubDate = LocalDateTime.parse(sDate);
            System.out.println(pubDate); // TODO: Remove debug code
            dataEntries.add(new DataEntry(
                    node.get("id").asInt(),
                    node.get("headline").toString(),
                    node.get("description").get("text").toString(),
                    pubDate));
        }
        return dataEntries;
    }
}