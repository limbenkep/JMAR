import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Objects;


public class SearchDialogController {
    private DataModel dataModel;
    private DataCollection searchReturn;
    private final String jobSearchAPI = "Platsbanken - JobSearch API";
    private final String historicalAdsAPI = "Platsbanken - Historical ads API";
    private final String[] searchMethods = {  jobSearchAPI, historicalAdsAPI };
    private final LocalDate dateFromRestriction = LocalDate.of(2016,1,1);
    private final LocalDate dateToRestriction = LocalDate.of(2021,12,31);
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton, saveSearch;
    @FXML
    private final Stage dialogStage;
    @FXML
    private AnchorPane searchProperties;
    @FXML
    private DatePicker dateFrom, dateTo;
    @FXML
    private Label labelDateTo, labelDateFrom;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressPercentage;
    @FXML
    private Label progressDownload;

    public SearchDialogController(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    public void initialize() {
        ObservableList<String> options =
                FXCollections.observableArrayList(searchMethods);
        comboBox.setItems(options);
        searchField.textProperty().addListener((observer, oldText, newText) -> {
            searchButton.setDisable(newText.isEmpty());
        });
        disableInvalidDates(dateFrom);
        disableInvalidDates(dateTo);
    }

    // Only shows dates between 2016-2021 (API restriction)
    private void disableInvalidDates(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty ||
                        date.compareTo(dateFromRestriction) < 0 ||
                        date.compareTo(dateToRestriction) > 0);
            }
        });
    }

    // Show search properties when a search method is selected.
    @FXML
    public void selectSearchMethod() {
        searchProperties.setVisible(true);
        if(Objects.equals(comboBox.getValue(), jobSearchAPI)) {
            dateFrom.setVisible(false);
            dateTo.setVisible(false);
            labelDateFrom.setVisible(false);
            labelDateTo.setVisible(false);
        } else if (Objects.equals(comboBox.getValue(), historicalAdsAPI)) {
            dateFrom.setVisible(true);
            dateTo.setVisible(true);
            labelDateFrom.setVisible(true);
            labelDateTo.setVisible(true);
        }
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }
    @FXML
    public void activateSearch() {

        if(Objects.equals(comboBox.getValue(), jobSearchAPI)) {
            searchThread(new JobSearchApiTask(searchField.getText()));
        } else if (Objects.equals(comboBox.getValue(), historicalAdsAPI)) {
            searchThread(new HistoricalAdsApiTask(searchField.getText(),
                    dateFrom.getValue(), dateTo.getValue()));
        }
    }
    private void searchThread(SearchTask search) {
        search.setOnSucceeded(e -> {
            searchReturn = search.getValue();
            saveSearch.setDisable(false);
        });
        // Update bar and percentage value
        search.progressProperty().addListener((observer, oldValue, newValue) -> {
            double i = ((double) newValue) * 100;
            progressPercentage.setText((int)i + "%");
            progressBar.setProgress((Double) newValue);
        });
        // Update number of posts fetched
        search.messageProperty().addListener((observer, oldString, newString)-> {
            progressDownload.setText(newString);
        });
        Thread thread = new Thread(search);
        thread.setDaemon(true);
        thread.start();
    }
    @FXML
    public void saveSearch() {
        dataModel.addDataCollection(searchReturn);
        saveSearch.setDisable(true);
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}