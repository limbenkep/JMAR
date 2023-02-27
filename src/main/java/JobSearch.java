import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class JobSearch extends JobTechAPISearch {
    private static final String SOURCE = "JobSearch API";
    private final LocalDateTime from, to;

    private String formattedDate_From, formattedDate_To;


    public JobSearch(String search,LocalDateTime from, LocalDateTime to) {
        super(search);
        this.source = SOURCE;
        // Get current time without milliseconds
        this.from = from;
        this.to = to;


        this.lastDate = String.valueOf(to);
        this.date = from.format(DateTimeFormatter.ISO_DATE)
                + " - "
                + to.format(DateTimeFormatter.ISO_DATE);

        // Convert to Format used by the API (YYYY-MM-DDTHH:MM:SS)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        formattedDate_From = from.format(formatter);
        formattedDate_To = to.format(formatter);
    }

    @Override
    protected String initialSearchString() {
        return "https://jobsearch.api.jobtechdev.se/search?"
                + "q=" + encodedSearch
                + "&limit=" + API_SUB_SEARCH_LIMIT
                + "&published-after=" + formattedDate_From
                + "&published-before=" + formattedDate_To
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