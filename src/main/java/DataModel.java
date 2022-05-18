import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {
    private final ObservableList<DataCollection> dataCollections;
    private int totalPosts;

    public DataModel() {
        this.dataCollections = FXCollections.observableArrayList();
    }

    public ObservableList<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public void addDataCollection(DataCollection dataCollection) {
        totalPosts += dataCollection.dataEntries().size();
        dataCollections.add(dataCollection);
    }

    public int getTotalPosts() {
        return totalPosts;
    }
}
