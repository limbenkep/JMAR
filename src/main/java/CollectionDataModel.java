import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CollectionDataModel {
    private final ObservableList<DataCollection> dataCollections;

    public CollectionDataModel() {
        this.dataCollections = FXCollections.observableArrayList();
    }

    public ObservableList<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public void addDataCollection(DataCollection dataCollection) {
        dataCollections.add(dataCollection);
    }

    public int getTotalPosts() {
        int totalPosts = 0;
        for(DataCollection collection : dataCollections) {
            totalPosts += collection.dataEntries().size();
        }
        return totalPosts;
    }

    public void removeEntry(DataCollection selected) {
        dataCollections.remove(selected);
    }

    /**
     * Deletes all entries in the list of retrieved text collection
     */
    public void clearDataModel(){
        dataCollections.clear();
    }
}
