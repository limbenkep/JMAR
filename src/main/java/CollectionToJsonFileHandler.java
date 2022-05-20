import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CollectionToJsonFileHandler extends FileHandler<DataCollection> {

    public CollectionToJsonFileHandler(Stage stage) {
        super(stage);
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
    }

    @Override
    public void saveFile(ObservableList<DataCollection> list) {
        File jsonFile = fileChooser.showSaveDialog(stage);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module()); // To enable writing LocalDateTime
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadFile(ObservableList<DataCollection> list) {
        File jsonFile = fileChooser.showOpenDialog(stage);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module()); // To enable reading LocalDateTime
        List<DataCollection> fileList;
        try {
            fileList = Arrays.asList(mapper.readValue(jsonFile, DataCollection[].class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        list.clear();
        list.addAll(fileList);
    }
}
