import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Kristian Angelin
 * @author Honorine Lima
 */
public class AnalysisDialogController {
    private final Stage stage;
    private CollectionDataModel collectionDataModel;
    private KeywordDataModel keywordDataModel;
    private ObservableList<SkillStat> stats;
    private String fileName;
    private KeywordAnalyser analyser;
    private ObservableList<ArrayList<String>> skillCombinations;
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
        analyser = new KeywordAnalyser(collectionDataModel, keywordDataModel, skillCombinations);
        stats = analyser.analyse();
        resultTable.setItems(stats);
    }

    public void setDataModels(CollectionDataModel collectionDataModel, KeywordDataModel keywordDataModel, ObservableList<ArrayList<String>> skillCombinations) {
        this.collectionDataModel = collectionDataModel;
        this.keywordDataModel = keywordDataModel;
        this.skillCombinations = skillCombinations;
    }

    @FXML
    public void close() {
        stage.close();
    }

    /**
     * Opens a page that displays a table of the titles of all the ads that
     * were a hit for each selected skill that was analysed.
     * The different skills can be selected from a drop down list at theh top left of the page.
     */
    @FXML
    public void viewResultCollection(){
        Stage adsViewStage = new Stage();
        ResultAdsListController controller = new ResultAdsListController(stage);
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

    /**
     * Presents the user with options to export to excel or csv.
     * Exports the statistics from the text analysis to a file of the chosen format
     * and opens the FileChooser to allow the user to choose where to save the file
     */
    @FXML
    public void exportResult(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Export");
        ButtonType excel = new ButtonType("Excel");
        ButtonType csv = new ButtonType("CSV");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.setContentText("Export result to:");
        dialog.getDialogPane().getButtonTypes().add(excel);
        dialog.getDialogPane().getButtonTypes().add(csv);
        dialog.getDialogPane().getButtonTypes().add(cancel);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == excel){
            ExportToExcel<SkillStat> excelResult = new ExportToExcel<>(stage);
            excelResult.export(resultTable, analyser.getCollectionNames() );
        } else if (result.get() == csv) {
            ExportResultToCSV csvResult = new ExportResultToCSV(stage);
            csvResult.saveFile(stats);
        }

    }

    /**
     * Called when export to CSV menu option is chosen
     */
    @FXML
    public void exportResultToCSV(){
        ExportResultToCSV csvResult = new ExportResultToCSV(stage);
        csvResult.saveFile(stats);
    }

    /**
     * Called when export to excel menu option is chosen
     */
    @FXML
    public void exportResultToExcel(){
        ExportToExcel<SkillStat> excelResult = new ExportToExcel<>(stage);
        excelResult.export(resultTable, analyser.getCollectionNames() );
    }

    @FXML
    public void compareSkillCombination(){
        
    }
}
