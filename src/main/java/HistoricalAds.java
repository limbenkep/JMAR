import java.time.LocalDateTime;

public class HistoricalAds extends JobTechAPISearch {
    private static final int API_REQUEST_TIMEOUT = 300;
    private final LocalDateTime from, to;
    public HistoricalAds(String search, LocalDateTime from, LocalDateTime to) {
        super(search);
        this.from = from;
        this.to = to;
        lastDate = String.valueOf(to);
    }

    @Override
    String initialSearchString() {
        return "https://dev-historical-api.jobtechdev.se/search?"
                + "q=" + encodedSearch
                + "&offset=" + searchOffset
                + "&limit=" + API_SUB_SEARCH_LIMIT
                + "&historical-from=" + from
                + "&historical-to=" + to
                + "&request-timeout="+ API_REQUEST_TIMEOUT
                + "&sort=pubdate-desc";
    }

    @Override
    String subSearchString() {
        return "https://dev-historical-api.jobtechdev.se/search?"
                + "q=" + encodedSearch
                + "&offset=" + searchOffset
                + "&limit=" + postLimit
                + "&historical-from=" + from
                + "&historical-to=" + lastDate
                + "&request-timeout="+ API_REQUEST_TIMEOUT
                + "&sort=pubdate-desc";
    }
}
