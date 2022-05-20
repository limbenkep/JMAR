import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class FileHandler<T> {

    protected FileChooser fileChooser;
    protected Stage stage;

    public FileHandler(Stage stage) {
        this.fileChooser = new FileChooser();
        this.stage = stage;
    }

    public abstract void saveFile(ObservableList<T> list);
    public abstract void loadFile(ObservableList<T> list);
}
