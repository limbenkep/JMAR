import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class HistoricalAds extends JobTechAPISearch {
    private static final String SOURCE = "HistoricalAds API";
    private static final int API_REQUEST_TIMEOUT = 300;
    private final LocalDateTime from, to;
    public HistoricalAds(String search, LocalDateTime from, LocalDateTime to) {
        super(search);
        this.source = SOURCE;
        this.from = from;
        this.to = to;
        this.lastDate = String.valueOf(to);
        this.date = from.format(DateTimeFormatter.ISO_DATE)
                    + " - "
                    + to.format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    protected String initialSearchString() {
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
    protected String subSearchString() {
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
