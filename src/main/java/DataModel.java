import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {
    private final ObservableList<DataCollection> dataCollections;

    public DataModel() {
        this.dataCollections = FXCollections.observableArrayList();
    }

    public ObservableList<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public void addDataCollection(DataCollection dataCollection) {
        dataCollections.add(dataCollection);
    }
}
