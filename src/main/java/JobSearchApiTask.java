import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;

public class JobSearchApiTask extends SearchTask {

    public JobSearchApiTask(String search) {
        super(search);
    }

    @Override
    DataCollection requestResponse() {
        DataEntry[] dataEntries;
        String rawJson = httpGetRequest("https://jobsearch.api.jobtechdev.se/search?q=" + search + "&limit=100");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(rawJson);
            dataEntries = new DataEntry[jsonNode.path("total").get("value").asInt()];
            Iterator<JsonNode> hits = jsonNode.path("hits").elements();
            int i = 0;
            // Parse results and store data entries.
            while (hits.hasNext()) {
                JsonNode node = hits.next();
                System.out.println(node.get("headline"));
                dataEntries[i] = new DataEntry(
                        node.get("id").asInt(),
                        node.get("headline").toString(),
                        node.get("description").get("text").toString());
                i++;
            }
        } catch (JsonProcessingException e) {
            System.out.println("Throw error!");
            throw new RuntimeException(e);
        }
        return new DataCollection(search, dataEntries);
    }
}