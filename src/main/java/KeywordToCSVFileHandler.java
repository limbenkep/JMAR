import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class KeywordToCSVFileHandler extends FileHandler<KeywordCollection> {
    private static final String DELIMITER = ";";

    public KeywordToCSVFileHandler(Stage stage) {
        super(stage);
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    }

    @Override
    public void saveFile(ObservableList<KeywordCollection> collection) {
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

    @Override
    public void loadFile(ObservableList<KeywordCollection> collection) {
        File csvFile = fileChooser.showOpenDialog(stage);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            String line;
            collection.clear();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(DELIMITER);
                collection.add(new KeywordCollection(parts[0], parts[1]));
            }
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
