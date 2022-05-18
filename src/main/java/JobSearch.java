import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class JobSearch extends JobTechAPISearch {
    private static final String SOURCE = "JobSearch API";
    public JobSearch(String search) {
        super(search);
        this.source = SOURCE;
        // Get current time without milliseconds
        this.lastDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME);
        this.date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    protected String initialSearchString() {
        return "https://jobsearch.api.jobtechdev.se/search?"
                + "q=" + encodedSearch
                + "&limit=" + API_SUB_SEARCH_LIMIT
                + "&sort=pubdate-desc";
    }

    @Override
    protected String subSearchString() {
        return "https://jobsearch.api.jobtechdev.se/search?"
                + "published-before=" + lastDate
                + "&q=" + encodedSearch
                + "&limit=" + postLimit
                + "&offset=" + searchOffset
                + "&sort=pubdate-desc";
    }


}