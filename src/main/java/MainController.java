import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainController {

    private DataModel dataModel;
    private final Stage stage;
    @FXML
    private TableView<DataCollection> tableView;
    @FXML
    private TableColumn<DataCollection, String> dataSet;
    @FXML
    private TableColumn<DataCollection, Integer> items;
    @FXML
    private Label totalPosts;
    @FXML
    private Label uniquePosts;

    public MainController(Stage stage) {
        this.stage = stage;
        dataModel = new DataModel();
    }
    @FXML
    private void initialize() {
        // Setup all listeners for changes
        dataSet.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
        items.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().dataEntries().size()).asObject());
        tableView.setItems(dataModel.getDataCollections());
        ObservableList<DataCollection> list = dataModel.getDataCollections();
        list.addListener((ListChangeListener<DataCollection>) change -> {
            System.out.println("Hej");
            totalPosts.setText("Total posts: " + dataModel.getTotalPosts());
            calculateUniquePosts(list);
        });
    }

    @FXML
    public void createSearchWindow() throws IOException {
        Stage dialogStage = new Stage();
        SearchDialogController controller = new SearchDialogController(dialogStage);
        controller.setDataModel(dataModel);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/search-dialog.fxml"));
        loader.setController(controller);

        Scene scene = new Scene(loader.<VBox>load());

        dialogStage.setTitle("Search data");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        dialogStage.setScene(scene);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.show();
    }

    private void calculateUniquePosts(ObservableList<DataCollection> list) {
        Set<Integer> unique = new HashSet<>(dataModel.getTotalPosts());
        for (DataCollection collection: list) {
            for(DataEntry entry  : collection.dataEntries()) {
                unique.add(entry.id());
            }
        }
        uniquePosts.setText("Unique posts: " + unique.size());
    }
}
