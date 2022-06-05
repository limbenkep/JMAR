import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class KeywordAnalyser {
    private final CollectionDataModel collectionDataModel;
    private final KeywordDataModel keywordDataModel;
    private final ObservableList<SkillStat> stats;
    private final String[] PREFIX = {" ", "n", "(", "/", "\\","-", ",", ".", ")", "!", "?" };
    private final String[] SUFFIX = {" ", ",", "\\", ".", "/",")", "?","- ", "!", "(", ";" };
    /*private final String[] APPEND_WORDS = {"utvecklare", "utveckling", "programmerare", "programmering", "kompetens",
                                        "-utvecklare", "-utveckling", "-programmerare", "-programmering", "-kompetens",
                                        "utvecklaren", "-utvecklaren", "språk", "-språk", "-language", "-ramverk",
                                        "-framework", "-baserad", "-based"};*/
    // More performant
    private final String[] APPEND_WORDS = {"utveck", "program", "kompetens",
            "-utveck", "-program", "-kompetens",
            "språk", "-språk", "-language", "-ramverk",
            "-framework", "-base"};

    public KeywordAnalyser(CollectionDataModel collectionDataModel, KeywordDataModel keywordDataModel) {
        this.collectionDataModel = collectionDataModel;
        this.keywordDataModel = keywordDataModel;
        this.stats = FXCollections.observableArrayList();
    }
    public ObservableList<SkillStat> analyse() {
        // For each DataCollection (ex. a search result or file)
        ArrayList<String> entryID = new ArrayList<String>(); // holds ids
        for(DataCollection collection : collectionDataModel.getDataCollections()) {
            // For each entry (ex. one post from search result or file content)
            for(DataCollectionEntry entry : collection.dataEntries()) {
                // Check for duplicate entries
                if(!entryID.contains(entry.id())) {
                    entryID.add(entry.id());
                    ArrayList<String> skills = new ArrayList<String>(); // Store skills found for entry
                    // For each keyword
                    for (KeywordCollection keywordCollection: keywordDataModel.getKeywordCollections()) {
                        // Skip if skill already stored
                        if(!skills.contains(keywordCollection.skill())) {
                            // Check if keyword is in text, and add to stats
                            if(keywordInText(entry.text().toLowerCase(), keywordCollection.keyword().toLowerCase())) {
                            //if(entry.text().toLowerCase().contains(keywordCollection.keyword().toLowerCase())) {
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
        }
        // Update percentages
        for(SkillStat stat: stats) {
            int index = stats.indexOf(stat);
            float percentage = (float) stat.count() / entryID.size() * 100; // Only calculate unique posts
            stats.set(index, new SkillStat(stat.skill(), stat.count(), percentage));
        }
        return stats;
    }

    // Check for a keyword in the text by adding suffixes and extra words
    private boolean keywordInText(String entry, String keyword) {
        boolean found = false;
        for(String suffix: SUFFIX) {
            for(String prefix: PREFIX) {
                if(entry.contains(prefix + keyword + suffix)) {
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }
        // Run with extra words
        if(!found) {
            for(String word: APPEND_WORDS) {
                if(entry.contains(keyword + word)) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
    // Check for a keyword in the text by adding suffixes and extra words
    /*private boolean keywordInText(String entry, String keyword) {
        boolean found = false;
        for(String suffix: SUFFIX) {
            for(String prefix: PREFIX) {
                if(entry.contains(prefix + keyword + suffix)) {
                    found = true;
                    break;
                }
            }
            if(found) {
                break;
            }
        }
        // Run with extra words
        if(!found) {
            for(String suffix: SUFFIX) {
                for(String prefix: PREFIX) {
                    for(String word: APPEND_WORDS) {
                        if(entry.contains(prefix + keyword + word + suffix)) {
                            found = true;
                            break;
                        }
                    }
                }
                if(found) {
                    break;
                }
            }
        }
        return found;
    }*/
}
