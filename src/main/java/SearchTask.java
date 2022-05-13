import javafx.concurrent.Task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class SearchTask extends Task<DataCollection> {
    protected String originalSearch;
    protected String encodedSearch;

    public SearchTask(String search) {
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

    // Creating
    abstract DataCollection requestResponse();

    protected String httpGetRequest(String request) {
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
}
