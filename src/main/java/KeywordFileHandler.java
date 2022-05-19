import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class KeywordFileHandler {
    private final FileChooser fileChooser;
    private final Stage stage;
    private static final String DELIMITER = ";";

    public KeywordFileHandler(Stage stage) {
        this.fileChooser = new FileChooser();
        this.stage = stage;
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    }
    public void saveCSVfile(ObservableList<KeywordCollection> collection) {
        File csvFile = fileChooser.showSaveDialog(stage);
        try {
            PrintWriter writer = new PrintWriter(csvFile);
            StringBuilder stringBuilder = new StringBuilder();
            for (KeywordCollection entry: collection) {
                stringBuilder.append(entry.keyword());
                stringBuilder.append(DELIMITER);
                stringBuilder.append(entry.skill());
                stringBuilder.append('\n');
            }
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadCSVfile(ObservableList<KeywordCollection> collection) {
        // Choose file
        File csvFile = fileChooser.showOpenDialog(stage);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            collection.clear();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                collection.add(new KeywordCollection(parts[0], parts[1]));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
