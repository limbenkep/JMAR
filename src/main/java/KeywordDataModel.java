import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class KeywordDataModel {
    private final ObservableList<KeywordCollection> keywordCollections;

    public KeywordDataModel() {
        this.keywordCollections = FXCollections.observableArrayList();
    }

    public ObservableList<KeywordCollection> getKeywordCollections() {
        return keywordCollections;
    }

    public void addEntry(KeywordCollection keywordCollection) {
        //totalKeywords = keywordCollections.size();
        keywordCollections.add(keywordCollection);
    }
    public void removeEntry(KeywordCollection entry) {
        //totalKeywords = keywordCollections.size();
        keywordCollections.remove(entry);
    }
}
