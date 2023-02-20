import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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

public class AnalysisDialogController {
    private final Stage stage;
    private CollectionDataModel collectionDataModel;
    private KeywordDataModel keywordDataModel;

    private KeywordAnalyser analyser;
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
        analyser = new KeywordAnalyser(collectionDataModel, keywordDataModel);
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

    @FXML
    public void viewResultCollection(){
        Stage adsViewStage = new Stage();
        ResultAdsListController controller = new ResultAdsListController(stage);
        System.out.println("g");
        controller.setDataModel(analyser.getResultDataModel());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/result-ads-list.fxml"));
        loader.setController(controller);
        Scene scene;
        try {
            scene = new Scene(loader.<VBox>load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        adsViewStage.setTitle("View ads from text analysis results");
        adsViewStage.initModality(Modality.WINDOW_MODAL);
        adsViewStage.initOwner(stage);
        adsViewStage.setScene(scene);
        adsViewStage.initStyle(StageStyle.UTILITY);
        adsViewStage.show();
    }
}
