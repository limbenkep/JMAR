import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ExportResultToCSV extends FileHandler<SkillStat>{
    private static final String DELIMITER = ";";
    public ExportResultToCSV(Stage stage) {
        super(stage);
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    }

    @Override
    public void saveFile(ObservableList<SkillStat> stats) {
        File csvFile = fileChooser.showSaveDialog(stage);
        try {
            PrintWriter writer = new PrintWriter(csvFile);
            StringBuilder stringBuilder = new StringBuilder();
            for (SkillStat stat: stats) {
                stringBuilder.append(stat.skill());
                stringBuilder.append(DELIMITER);
                stringBuilder.append(stat.count());
                stringBuilder.append(DELIMITER);
                stringBuilder.append(stat.count());
                stringBuilder.append('\n');
            }
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void loadFile(ObservableList<SkillStat> stats) {

    }

}
