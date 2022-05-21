import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class KeywordAnalyser {
    private final CollectionDataModel collectionDataModel;
    private final KeywordDataModel keywordDataModel;
    private final ObservableList<SkillStat> stats;

    public KeywordAnalyser(CollectionDataModel collectionDataModel, KeywordDataModel keywordDataModel) {
        this.collectionDataModel = collectionDataModel;
        this.keywordDataModel = keywordDataModel;
        this.stats = FXCollections.observableArrayList();
    }
    public ObservableList<SkillStat> analyse() {
        // For each DataCollection (ex. a search result or file)
        for(DataCollection collection : collectionDataModel.getDataCollections()) {
            // For each entry (ex. one post from search result or file content)
            for(DataCollectionEntry entry : collection.dataEntries()) {
                ArrayList<String> skills = new ArrayList<String>(); // Store skills found for entry
                // For each keyword
                for (KeywordCollection keywordCollection: keywordDataModel.getKeywordCollections()) {
                    // Skip if skill already stored
                    if(!skills.contains(keywordCollection.skill())) {
                        // Check if keyword is in text, and add to stats
                        if(entry.text().toLowerCase().contains(keywordCollection.keyword().toLowerCase())) {
                            skills.add(keywordCollection.skill()); // Store skill
                            int count = 1;
                            SkillStat newStat = new SkillStat(keywordCollection.skill(), count, 0); // TODO: NEEDED?
                            boolean skillAdded = false;
                            // Check if skill is a registered stat
                            for(SkillStat stat: stats) {
                                // Add to stat if already registered
                                if(stat.skill().equals(newStat.skill())) {
                                    int index = stats.indexOf(stat);
                                    count += stat.count();
                                    stats.set(index, new SkillStat(stat.skill(), count, 0));
                                    skillAdded = true;
                                    break;
                                }
                            }
                            // If skill is not present create new entry
                            if(!skillAdded) {
                                stats.add(newStat);
                            }
                        }
                    }
                }
            }
        }
        // Update percentages
        for(SkillStat stat: stats) {
            int index = stats.indexOf(stat);
            float percentage = (float) stat.count() / collectionDataModel.getTotalPosts() * 100;
            stats.set(index, new SkillStat(stat.skill(), stat.count(), percentage));
        }
        return stats;
    }
}
