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

    private DataModel dataModel;
    private final Stage stage;
    FileChooser pdfFileChooser;
    @FXML
    private TableView<DataCollection> tableView;
    @FXML
    private TableColumn<DataCollection, String> dataName;
    @FXML
    private TableColumn<DataCollection, Integer> items;
    @FXML
    private TableColumn<DataCollection, String> dataSource;
    @FXML
    private TableColumn<DataCollection, String> dataDate;
    @FXML
    private Label totalPosts;
    @FXML
    private Label uniquePosts;

    public MainController(Stage stage) {
        this.stage = stage;
        this.dataModel = new DataModel();
        this.pdfFileChooser = new FileChooser();
    }
    @FXML
    private void initialize() {
        // Setup all listeners for changes
        dataName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title()));
        items.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().dataEntries().size()).asObject());
        dataSource.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().source()));
        dataDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().date()));
        tableView.setItems(dataModel.getDataCollections());
        ObservableList<DataCollection> list = dataModel.getDataCollections();
        list.addListener((ListChangeListener<DataCollection>) change -> {
            System.out.println("Hej");
            totalPosts.setText("Total posts: " + dataModel.getTotalPosts());
            calculateUniquePosts(list);
        });

        pdfFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
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

    @FXML
    public void openPDF() {
        // Choose file
        File pdfFile = pdfFileChooser.showOpenDialog(stage);
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

        int id = text.hashCode(); // Generate id for entry to avoid duplicates
        ArrayList<DataEntry> pdfEntry = new ArrayList<>();
        pdfEntry.add(new DataEntry(id, pdfFile.getName(), text, LocalDateTime.now()));
        // Add entry to collection
        dataModel.addDataCollection(new DataCollection(pdfFile.getName(),
                                        pdfEntry,
                                        "PDF-file",
                                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)));
        System.out.println(text); // TODO: remove debug text
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
