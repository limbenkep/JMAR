import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Honorine Lima
 *
 * This class displays the  the titles of the ads that were a hit for each skills on a table
 * and each ad row can be clicked to display the full content of the ad
 */
public class ResultAdsListController {
    private final Stage stage;
    private CollectionDataModel resultDataModel;
    private ObservableList<SkillStat> stats;
    private ObservableList<DataCollectionEntry> adsEntries;
    private ObservableList<String> comboboxOptions;

    @FXML
    private ComboBox<String> resultCollectionOptions;
    @FXML
    private TableView<DataCollectionEntry> resultAdsTable;

    @FXML
    private TextField locationTextField;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField textTextField;

    @FXML
    private TableColumn<DataCollectionEntry, String> titleColumn;

    @FXML
    private TableColumn<DataCollectionEntry, String> locationColumn;

    @FXML
    private CheckBox Title_CheckBox;
    @FXML
    private CheckBox Description_CheckBox;
    @FXML
    private TextField furtherAnalysisKeyword_TextField;
    @FXML
    private Button furtherAnalysis_Button;

    public ResultAdsListController(Stage stage) {
        this.stage = stage;
        comboboxOptions = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        resultCollectionOptions.setEditable(true);
        resultCollectionOptions.setItems(comboboxOptions);
        resultCollectionOptions.getSelectionModel().selectFirst();
        resultAdsTable.setItems(adsEntries);
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));

        // Adding Locations to the Table (if location is null --> convert to "")
        locationColumn.setCellValueFactory(cellData -> {
            String location = cellData.getValue().location();
            return new SimpleStringProperty(location == null ? "" : location);
        });

        EventHandler<MouseEvent> onClick = this::handleTableRowMouseDoubleClick;
        resultAdsTable.setRowFactory(param -> {
            TableRow<DataCollectionEntry> row = new TableRow<>();
            row.setOnMouseClicked(onClick);
            return row;
        });
        resultCollectionOptions.valueProperty().addListener((observableValue, s, t1) -> {
            int index = comboboxOptions.indexOf(t1);
            adsEntries = FXCollections.observableArrayList(resultDataModel.getDataCollections().get(index).dataEntries());
            resultAdsTable.setItems(adsEntries);
        });

        ShowAnalyzeButtonWhenSelecting();

    }

    private void ShowAnalyzeButtonWhenSelecting() {
        // Create a BooleanProperty to represent whether the button should be enabled
        BooleanProperty isButtonEnabled = new SimpleBooleanProperty(false);

// Add a listener to the textField to update the isButtonEnabled property
        furtherAnalysisKeyword_TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonState(isButtonEnabled, furtherAnalysisKeyword_TextField, Title_CheckBox, Description_CheckBox);
        });

        // Add listeners to the checkBox objects to update the isButtonEnabled property
        Title_CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonState(isButtonEnabled, furtherAnalysisKeyword_TextField, Title_CheckBox, Description_CheckBox);
        });
        Description_CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonState(isButtonEnabled, furtherAnalysisKeyword_TextField, Title_CheckBox, Description_CheckBox);
        });

        // Set the initial state of the button
        furtherAnalysis_Button.disableProperty().bind(isButtonEnabled.not());

    }

    private void updateButtonState(BooleanProperty isButtonEnabled, TextField textField, CheckBox checkBox1, CheckBox checkBox2) {
        boolean textFieldNotEmpty = !textField.getText().isEmpty();
        boolean checkBoxSelected = checkBox1.isSelected() || checkBox2.isSelected();
        isButtonEnabled.set(textFieldNotEmpty && checkBoxSelected);
    }

    /**
     * Sets the the job ads entries from the text analysis to be displayed
     * @param dataModel
     */
    public void setDataModel(CollectionDataModel dataModel){
        resultDataModel = dataModel;
        boolean is = resultDataModel == null;
        //Set default as the first skill ads entries
        this.adsEntries = FXCollections.observableArrayList(resultDataModel.getDataCollections().get(0).dataEntries());
        for (DataCollection collection : dataModel.getDataCollections()) {
            comboboxOptions.add(collection.title());
        }
    }

    /**
     * Called when an ad on the ads Table is clicked.
     * When double-clicked, the ad is opened to display it full content
     * @param event click event
     */
    private void handleTableRowMouseDoubleClick(MouseEvent event){
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            @SuppressWarnings("unchecked")
            TableRow<DataCollectionEntry> row = (TableRow<DataCollectionEntry>) event.getSource();

            if (!row.isEmpty() && row.getItem() != null) {
                System.out.println("Row: " + row.getItem());
                DataCollectionEntry entry = row.getItem();

                Stage adPageStage = new Stage();
                ScrollPane root = new ScrollPane();
                Scene scene = new Scene(root, 500, 500);
                Text text = new Text(
                        "Ad Id: " + entry.id().replace("\"", "") + "\n\n"
                                + "Title: " + entry.title().replace("\"", "") + "\n\n"
                                + "Description" + "\n" + entry.text().replace("\\n", "\n").replace("\\r", "").replace("\"", ""));
                text.wrappingWidthProperty().bind(scene.widthProperty());
                root.setFitToWidth(true);
                root.setContent(text);
                adPageStage.setTitle("View ad content");
                adPageStage.setScene(scene);
                adPageStage.show();
                event.consume();
            }
        }
    }


    @FXML
    public void close() {
        stage.close();
    }

    @FXML
    public void filterResults()
    {
        String locationWordToFilter = locationTextField.getText().toLowerCase();
        String titleWordToFilter = titleTextField.getText().toLowerCase();
        String textWordToFilter = textTextField.getText().toLowerCase();


        ObservableList<DataCollectionEntry> filteredEntries = FXCollections.observableArrayList();
        for (DataCollectionEntry entry : adsEntries) {
            if (entry.location().toLowerCase().contains(locationWordToFilter) &&
                    entry.text().toLowerCase().contains(textWordToFilter) &&
                    entry.title().toLowerCase().contains(titleWordToFilter)) {
                filteredEntries.add(entry);
            }
        }

        resultAdsTable.setItems(filteredEntries);

    }

    @FXML
    public void performFurtherAnalysis() {
        boolean titleSelected = Title_CheckBox.isSelected();
        boolean descriptionSelected = Description_CheckBox.isSelected();
        String keyword = furtherAnalysisKeyword_TextField.getText().toLowerCase();

        // Required:
        // Take keyword, and two selections, open new analyze page and insert results
        ObservableList<DataCollectionEntry> furtherAnalysisResults = FXCollections.observableArrayList();


        for (DataCollectionEntry entry : adsEntries) {
            if (titleSelected && descriptionSelected) {
                if (entry.title().toLowerCase().contains(keyword) || entry.text().toLowerCase().contains(keyword))
                    furtherAnalysisResults.add(entry);

            } else if (titleSelected && !descriptionSelected) {
                if (entry.title().toLowerCase().contains(keyword))
                    furtherAnalysisResults.add(entry);
            } else if (!titleSelected && descriptionSelected) {
                if (entry.text().toLowerCase().contains(keyword))
                    furtherAnalysisResults.add(entry);
            }
        }


        Stage adsViewStage = new Stage();
        FurtherAnalysisController controller = new FurtherAnalysisController(stage);
        controller.setDataModel(furtherAnalysisResults);
        controller.setKeyword(keyword);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/furtherAnalysis.fxml"));
        loader.setController(controller);
        Scene scene;
        try {
            scene = new Scene(loader.<VBox>load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        adsViewStage.setTitle("Analyse Results (Keyword: '" + keyword + "')");
        adsViewStage.initModality(Modality.WINDOW_MODAL);
        adsViewStage.initOwner(stage);
        adsViewStage.setScene(scene);
        adsViewStage.initStyle(StageStyle.UTILITY);
        adsViewStage.show();
    }


}
