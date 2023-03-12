import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FurtherAnalysisController {
    private final Stage stage;

    @FXML
    private Button closeButton;

    @FXML
    private TableView<DataCollectionEntry> resultAdsTable;
    @FXML
    private TableColumn<DataCollectionEntry, String> titleColumn;

    @FXML
    private TableColumn<DataCollectionEntry, String> descriptionColumn;

    @FXML
    private TextArea selectedAdDescription_TextArea;

    private ObservableList<DataCollectionEntry> furtherAnalysisResults;

    private String keyword;

    public FurtherAnalysisController(Stage stage) {
        this.stage = stage;
    }
    public void setDataModel(ObservableList<DataCollectionEntry> adsEntries)
    {
        furtherAnalysisResults = adsEntries;
    }

    public void setKeyword(String key)
    {
        keyword = key;
    }

    @FXML
    public void initialize() {
        resultAdsTable.setItems(furtherAnalysisResults);
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().text()));

    }

    @FXML
    public void showAdDescription()
    {
        DataCollectionEntry rowData = resultAdsTable.getSelectionModel().getSelectedItem();
        String desc = rowData.text().replaceAll("\\\\n","\n");
        selectedAdDescription_TextArea.setText(desc);
    }

    @FXML
    public void close()
    {
        stage.close();
    }
}
