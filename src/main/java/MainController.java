import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainController {

    private final CollectionDataModel collectionDataModel;
    private final KeywordDataModel keywordDataModel;
    private final Stage stage;
    @FXML
    private TableView<DataCollection> dataTable;
    @FXML
    private TableColumn<DataCollection, String> dataName;
    @FXML
    private TableColumn<DataCollection, Integer> items;
    @FXML
    private TableColumn<DataCollection, String> dataSource;
    @FXML
    private TableColumn<DataCollection, String> dataDate;
    @FXML
    private TableView<KeywordCollection> keywordTable;
    @FXML
    private TableColumn<KeywordCollection, String> keywordColumn;
    @FXML
    private TableColumn<KeywordCollection, String> skillColumn;
    @FXML
    private Label totalPosts;
    @FXML
    private Label uniquePosts;
    @FXML
    private Button compareButton;
    @FXML
    private TextField keywordEntry;
    @FXML
    private TextField skillEntry;
    @FXML
    private Label errorMsg;
    @FXML
    private Label totalKeywords;
    @FXML
    private Label totalUniqueSkills;

    public MainController(Stage stage) {
        this.stage = stage;
        this.collectionDataModel = new CollectionDataModel();
        this.keywordDataModel = new KeywordDataModel();
    }

    @FXML
    private void initialize() {
        // Setup listeners for collection table
        dataName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
        items.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().dataEntries().size()).asObject());
        dataSource.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().source()));
        dataDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().date()));
        dataTable.setItems(collectionDataModel.getDataCollections());
        // Watch for changes to update total posts and unique posts.
        ObservableList<DataCollection> dataCollection = collectionDataModel.getDataCollections();
        dataCollection.addListener((ListChangeListener<DataCollection>) change -> {
            totalPosts.setText("Total posts: " + collectionDataModel.getTotalPosts());
            calculateUniquePosts(dataCollection);
        });
        // Setup listeners for keyword table
        keywordTable.setItems(keywordDataModel.getKeywordCollections());
        keywordColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().keyword()));
        skillColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().skill()));

        ObservableList<KeywordCollection> keywordCollection = keywordDataModel.getKeywordCollections();
        keywordCollection.addListener((ListChangeListener<KeywordCollection>) change -> {
            totalKeywords.setText("Total keywords: " + keywordCollection.size());
            calculateUniqueSkills(keywordCollection);
        });
    }

    @FXML
    public void createSearchWindow() throws IOException {
        Stage dialogStage = new Stage();
        SearchDialogController controller = new SearchDialogController(dialogStage);
        controller.setDataModel(collectionDataModel);

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

    @FXML
    public void openPDF() {
        // Setup file chooser to only look for PDFs
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File pdfFile = fileChooser.showOpenDialog(stage);
        // Generate text from pdf file
        PDDocument document;
        PDFTextStripper stripper;
        String text;
        try {
            document = PDDocument.load(pdfFile);
            stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (IOException | NullPointerException e) {
            return;
        }

        int id = text.hashCode(); // Generate id for keyword to avoid duplicates
        ArrayList<DataCollectionEntry> pdfEntry = new ArrayList<>();
        pdfEntry.add(new DataCollectionEntry(id, pdfFile.getName(), text, LocalDateTime.now()));
        // Add keyword to collection
        collectionDataModel.addDataCollection(new DataCollection(pdfFile.getName(),
                                        pdfEntry,
                                        "PDF-file",
                                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)));
    }

    private void calculateUniquePosts(ObservableList<DataCollection> list) {
        Set<Integer> unique = new HashSet<>(collectionDataModel.getTotalPosts());
        for (DataCollection collection: list) {
            for(DataCollectionEntry entry  : collection.dataEntries()) {
                unique.add(entry.id());
            }
        }
        uniquePosts.setText("Unique posts: " + unique.size());
    }

    private void calculateUniqueSkills(ObservableList<KeywordCollection> list) {
        Set<String> unique = new HashSet<>(collectionDataModel.getTotalPosts());
        for (KeywordCollection collection: list) {
            unique.add(collection.skill());
        }
        totalUniqueSkills.setText("Total unique skills: " + unique.size());
    }

    @FXML
    private void startCompare() {
        if(!dataTable.getItems().isEmpty() && !keywordTable.getItems().isEmpty()) {
            Stage dialogStage = new Stage();
            AnalysisDialogController controller = new AnalysisDialogController(dialogStage);
            controller.setDataModels(collectionDataModel, keywordDataModel);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/analysis-result-dialog.fxml"));
            loader.setController(controller);

            Scene scene = null;
            try {
                scene = new Scene(loader.<VBox>load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dialogStage.setTitle("Compare data (only unique entries)");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            dialogStage.setScene(scene);
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.show();
        }
    }

    // Delete selected keyword entry
    @FXML
    private void tableKeyPressed(KeyEvent event) {
        // Delete selected keyword
        System.out.println("SOURCE: " + event.getSource());
        if(event.getCode().equals(KeyCode.DELETE)) {
            if(event.getSource() == keywordTable) {
                KeywordCollection selected = keywordTable.getSelectionModel().getSelectedItem();
                keywordDataModel.removeEntry(selected);
            } else if(event.getSource() == dataTable) {
                DataCollection selected = dataTable.getSelectionModel().getSelectedItem();
                collectionDataModel.removeEntry(selected);
            }
        }
    }

    // Add keyword entry
    @FXML
    private void addEntry() {
        // Check that TextFields are not empty
        if(!keywordEntry.getText().isBlank() && !skillEntry.getText().isBlank()) {
            keywordDataModel.addEntry(new KeywordCollection(keywordEntry.getText(), skillEntry.getText()));
            errorMsg.setVisible(false);
        } else {
            errorMsg.setVisible(true); // Show error message in GUI
        }
    }

    @FXML
    private void saveCollection() {
        CollectionToJsonFileHandler fileHandler = new CollectionToJsonFileHandler(stage);
        fileHandler.saveFile(collectionDataModel.getDataCollections());
    }

    @FXML
    private void openCollection() {
        CollectionToJsonFileHandler fileHandler = new CollectionToJsonFileHandler(stage);
        fileHandler.loadFile(collectionDataModel.getDataCollections());
    }

    @FXML
    private void saveSkillKeywordsAs() {
        KeywordToCSVFileHandler fileHandler = new KeywordToCSVFileHandler(stage);
        fileHandler.saveFile(keywordDataModel.getKeywordCollections());
    }

    @FXML
    private void openSkillKeywords() {
        KeywordToCSVFileHandler fileHandler = new KeywordToCSVFileHandler(stage);
        fileHandler.loadFile(keywordDataModel.getKeywordCollections());
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
    }
}
