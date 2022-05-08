import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class SearchDialogController {
    DataCollection searchReturn;
    String[] searchMethods = {  "Platsbanken - JobSearch API" };
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button saveSearch;
    @FXML
    private final Stage dialogStage;
    @FXML
    private AnchorPane searchProperties;

    public SearchDialogController(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    public void initialize() {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        searchMethods
                );
        comboBox.setItems(options);
        searchField.textProperty().addListener((observer, oldText, newText) -> {
            searchButton.setDisable(newText.isEmpty());
        });
    }

    // Show search properties when a search method is selected.
    @FXML
    public void selectSearchMethod() {
        searchProperties.setVisible(true);
    }

    @FXML
    public void activateSearch() {
        JobSearchApiTask search = new JobSearchApiTask(searchField.getText());
        search.setOnSucceeded(e -> {
            searchReturn = search.getValue();
            saveSearch.setDisable(false);
        });
        Thread thread = new Thread(search);
        thread.setDaemon(true);
        thread.start();
    }
    @FXML
    public void saveSearch() {
        System.out.println("Save is not implemented!");
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}
