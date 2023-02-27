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
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private TableColumn<DataCollectionEntry, String> titleColumn;
    @FXML
    private TableColumn<DataCollectionEntry, LocalDateTime> dateColumn;

    public ResultAdsListController(Stage stage) {
        this.stage = stage;
        comboboxOptions = FXCollections.observableArrayList();
    }
    @FXML
    public void initialize(){
        resultCollectionOptions.setItems(comboboxOptions);
        resultCollectionOptions.getSelectionModel().selectFirst();
        resultAdsTable.setItems(adsEntries);
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
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
    public void setDataModel(CollectionDataModel dataModel){
        resultDataModel = dataModel;
        boolean is = resultDataModel==null;
        //Set default as the first skill ads entries
        this.adsEntries = FXCollections.observableArrayList(resultDataModel.getDataCollections().get(0).dataEntries());
        for(DataCollection collection: dataModel.getDataCollections()){
            comboboxOptions.add(collection.title());
        }
    }

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
}
