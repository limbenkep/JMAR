public class JobSearchApiTask extends SearchTask {

    public JobSearchApiTask(String search) {
        super(search);
    }

    @Override
    String requestResponse() {
        return httpGetRequest("https://jobsearch.api.jobtechdev.se/search?q=" + search);
    }
}
