import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class AnalysisDialogController {
    private final Stage stage;
    private CollectionDataModel collectionDataModel;
    private KeywordDataModel keywordDataModel;
    @FXML
    private TableView<SkillStat> resultTable;
    @FXML
    private TableColumn<SkillStat, String> skillColumn;
    @FXML
    private TableColumn<SkillStat, Integer> entriesColumn;
    @FXML
    private TableColumn<SkillStat, Float> percentageColumn;
    public AnalysisDialogController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Prepare table columns
        skillColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().skill()));
        entriesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().count()).asObject());
        percentageColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().percentage()).asObject());

        // Analyse and update table
        KeywordAnalyser analyser = new KeywordAnalyser(collectionDataModel, keywordDataModel);
        ObservableList<SkillStat> stats = analyser.analyse();
        resultTable.setItems(stats);
    }

    public void setDataModels(CollectionDataModel collectionDataModel, KeywordDataModel keywordDataModel) {
        this.collectionDataModel = collectionDataModel;
        this.keywordDataModel = keywordDataModel;
    }

    @FXML
    public void close() {
        stage.close();
    }
}
