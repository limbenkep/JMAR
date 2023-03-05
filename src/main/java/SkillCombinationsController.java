import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;

public class SkillCombinationsController {
    private final Stage stage;
    private ObservableList<String> skillsList;
    private ObservableList<String> skillOptions;
    private ObservableList<ArrayList<String>> skillCombinations;
    @FXML
    private CheckComboBox<String> skillsChoices;

    @FXML
    private ListView<String> skillCombinationList;

    public SkillCombinationsController(Stage stage) {
        this.stage = stage;
        this.skillsList = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize(){
        skillsChoices.getItems().addAll(skillOptions);
        skillCombinationList.setItems(skillsList);
    }

    public void setSkillOptions(ObservableList<String> skills, ObservableList<ArrayList<String>> combinations){
        skillOptions = skills;
        skillCombinations = combinations;
    }

    @FXML
    public void addSkillCombination(){
        ObservableList<String> list = skillsChoices.getCheckModel().getCheckedItems();
        skillCombinations.add(new ArrayList<>(list));
        StringBuilder skillCombo = new StringBuilder();
        for(String s: list){
            skillCombo.append(s).append(", ");
        }
        skillCombo.delete(skillCombo.length()-2, skillCombo.length()-1);
        skillsList.add(skillCombo.toString());
    }

    @FXML
    public void close(){
        stage.close();
    }
}
