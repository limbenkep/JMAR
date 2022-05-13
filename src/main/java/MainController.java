import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class MainController {

    private DataModel dataModel;
    private final Stage stage;
    @FXML
    private TableView<DataCollection> dataSet01;
    @FXML
    private TableColumn<DataCollection, String> dataSet;
    @FXML
    private TableColumn<DataCollection, Integer> items;
    public MainController(Stage stage) {
        this.stage = stage;
        dataModel = new DataModel();
    }
    @FXML
    private void initialize() {
        dataSet.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
        items.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().dataEntries().size()).asObject());
        dataSet01.setItems(dataModel.getDataCollections());

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
}
