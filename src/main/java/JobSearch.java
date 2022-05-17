import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class JobSearch extends JobTechAPISearch {

    public JobSearch(String search) {
        super(search);
        // Get current time without milliseconds
        lastDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    String initialSearchString() {
        return "https://jobsearch.api.jobtechdev.se/search?"
                + "q=" + encodedSearch
                + "&limit=" + API_SUB_SEARCH_LIMIT
                + "&sort=pubdate-desc";
    }

    @Override
    String subSearchString() {
        return "https://jobsearch.api.jobtechdev.se/search?"
                + "published-before=" + lastDate
                + "&q=" + encodedSearch
                + "&limit=" + postLimit
                + "&offset=" + searchOffset
                + "&sort=pubdate-desc";
    }


}