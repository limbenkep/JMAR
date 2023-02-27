import javafx.beans.property.SimpleStringProperty;
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
<<<<<<< HEAD
import javafx.stage.StageStyle;
import org.w3c.dom.Text;
=======
import javafx.stage.StageStyle;
import org.w3c.dom.Text;
>>>>>>> Filter_by_location

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
    private TableColumn<DataCollectionEntry, String> titleColumn;

    @FXML
    private TableColumn<DataCollectionEntry, String> locationColumn;
    @FXML
    private TableColumn<DataCollectionEntry, LocalDateTime> dateColumn;

    public ResultAdsListController(Stage stage) {
        this.stage = stage;
        comboboxOptions = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        resultCollectionOptions.setItems(comboboxOptions);
        resultCollectionOptions.getSelectionModel().selectFirst();
        resultAdsTable.setItems(adsEntries);
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));

        // Adding Locations to the Table (if location is null --> convert to "")
        locationColumn.setCellValueFactory(cellData -> {
            String location = cellData.getValue().location();
            return new SimpleStringProperty(location == null ? "" : location);
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY/MM/DD");
        dateColumn.setCellFactory(cellData -> {
            return new TableCell<DataCollectionEntry, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        //setText(formatter.format(item));
                        setText(String.format(item.format(formatter)));
                    }
                }
            };
        });
        EventHandler<MouseEvent> onClick = this::handleTableRowMouseDoubleClick;
        resultAdsTable.setRowFactory(param -> {
            TableRow<DataCollectionEntry> row = new TableRow<>();
            row.setOnMouseClicked(onClick);
            return row;
        });


    }

<<<<<<< HEAD
    /**
     * Sets the the job ads entries from the text analysis to be displayed
     * @param dataModel
     */
    public void setDataModel(CollectionDataModel dataModel){
=======
    public void setDataModel(CollectionDataModel dataModel) {
>>>>>>> Filter_by_location
        resultDataModel = dataModel;
        boolean is = resultDataModel == null;
        //Set default as the first skill ads entries
        this.adsEntries = FXCollections.observableArrayList(resultDataModel.getDataCollections().get(0).dataEntries());
        for (DataCollection collection : dataModel.getDataCollections()) {
            comboboxOptions.add(collection.title());
        }
    }

<<<<<<< HEAD
    /**
     * Called when an ad on the ads Table is clicked.
     * When double clicked, the ad is opened to display it full content
     * @param event click event
     */
    private void handleTableRowMouseDoubleClick(MouseEvent event){
=======
    private void handleTableRowMouseDoubleClick(MouseEvent event) {
>>>>>>> Filter_by_location
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
                        "Ad Id: " + entry.id().replace("\"", "") + "\n"
                                + "Title: " + entry.title().replace("\"", "") + "\n"
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
    public void filterResultsByLocation()
    {
        String locationWordToFilter = locationTextField.getText().toLowerCase();

        ObservableList<DataCollectionEntry> filteredEntries = FXCollections.observableArrayList();
        for (DataCollectionEntry entry : adsEntries) {
            if (entry.location().toLowerCase().contains(locationWordToFilter)) {
                filteredEntries.add(entry);
            }
        }

        resultAdsTable.setItems(filteredEntries);

    }

}
