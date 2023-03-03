import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


public class SearchDialogController {
    private CollectionDataModel collectionDataModel;
    private DataCollection searchReturn;
    private final String jobSearchAPI = "Platsbanken - JobSearch API";
    private final String historicalAdsAPI = "Platsbanken - Historical ads API";
    private final String[] searchMethods = {jobSearchAPI, historicalAdsAPI};
    private final LocalDate dateFromRestriction = LocalDate.of(2016, 1, 1);
    private final LocalDate dateToRestriction = LocalDate.now();
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
        ObservableList<String> options = FXCollections.observableArrayList(searchMethods);
        comboBox.setItems(options);
        disableInvalidDates(dateFrom);
        disableInvalidDates(dateTo);


        // Enabling Search Button after Entering Text in searchfield and selecting two dates
        BooleanBinding isButtonEnabled = Bindings.createBooleanBinding(() -> {
            boolean isTextNotEmpty = !searchField.getText().isEmpty();
            boolean areDatesSelected = dateFrom.getValue() != null && dateTo.getValue() != null;
            return isTextNotEmpty && areDatesSelected;
        }, searchField.textProperty(), dateFrom.valueProperty(), dateTo.valueProperty());

        // Bind the button's visible property to the BooleanBinding
        searchButton.disableProperty().bind(isButtonEnabled.not());

    }

    // Only shows dates between 2016-2021 (API restriction)
    private void disableInvalidDates(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(dateFromRestriction) < 0 || date.compareTo(dateToRestriction) > 0);
            }
        });
    }

    // Show search properties when a search method is selected.
    @FXML
    public void selectSearchMethod() {
        searchProperties.setVisible(true);
        if (Objects.equals(comboBox.getValue(), jobSearchAPI)) {
            dateFrom.setVisible(true);      // converted from false to true
            dateTo.setVisible(true);        // converted from false to true
            labelDateFrom.setVisible(true);
            labelDateTo.setVisible(true);

        } else if (Objects.equals(comboBox.getValue(), historicalAdsAPI)) {
            dateFrom.setVisible(true);
            dateTo.setVisible(true);
            labelDateFrom.setVisible(true);
            labelDateTo.setVisible(true);
        }
    }

    public void setDataModel(CollectionDataModel collectionDataModel) {
        this.collectionDataModel = collectionDataModel;
    }

    @FXML
    public void activateSearch() {
        LocalDateTime from = LocalDateTime.now().minusYears(3);
        LocalDateTime to = LocalDateTime.now();
        if (dateFrom.getValue() != null) {
            from = dateFrom.getValue().atStartOfDay();
        }
        if (dateTo.getValue() != null) {
            to = dateTo.getValue().atTime(23, 59, 59);
        }

        if (Objects.equals(comboBox.getValue(), jobSearchAPI)) {
            searchThread(new JobSearch(searchField.getText(), from, to));
        } else if (Objects.equals(comboBox.getValue(), historicalAdsAPI)) {
            searchThread(new HistoricalAds(searchField.getText(), from, to));
        }
    }

    private void searchThread(JobTechAPISearch search) {
        search.setOnSucceeded(e -> {
            searchReturn = search.getValue();
            saveSearch.setDisable(false);
        });
        // Update bar and percentage value
        search.progressProperty().addListener((observer, oldValue, newValue) -> {
            double i = ((double) newValue) * 100;
            progressPercentage.setText((int) i + "%");
            progressBar.setProgress((Double) newValue);
        });
        // Update number of posts fetched
        search.messageProperty().addListener((observer, oldString, newString) -> {
            progressDownload.setText(newString);
        });
        Thread thread = new Thread(search);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void saveSearch() {
        collectionDataModel.addDataCollection(searchReturn);
        saveSearch.setDisable(true);
    }

    @FXML
    public void cancel() {
        dialogStage.close();
    }
}