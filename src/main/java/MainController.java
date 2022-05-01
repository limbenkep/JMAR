import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainController {

    private final Stage stage;
    public MainController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void CreateSearchWindow() throws IOException {
        Stage dialogStage = new Stage();
        SearchDialogController controller = new SearchDialogController(dialogStage);

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
}
