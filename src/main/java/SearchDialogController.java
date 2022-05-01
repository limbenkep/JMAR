import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SearchDialogController {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    Stage dialogStage;

    public SearchDialogController(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    public void initialize() {
        searchField.textProperty().addListener((observer, oldText, newText) -> {
            searchButton.setDisable(newText.isEmpty());
        });
    }

    @FXML
    public void ActivateSearch() {
        System.out.println("Dummy search!");
    }

    @FXML
    public void Cancel() {
        dialogStage.close();
    }
}
