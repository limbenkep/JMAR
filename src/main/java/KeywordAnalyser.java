import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordAnalyser {
    private final CollectionDataModel collectionDataModel;
    private final CollectionDataModel resultDataModel;
    private final KeywordDataModel keywordDataModel;
    private final ObservableList<SkillStat> stats;
    private ObservableList<ArrayList<String>> skillCombinations;
    private String resultSource  ="Result collection from analysing collections ";

    private final ArrayList<ArrayList<DataCollectionEntry>> dataEntries;
    private final ArrayList<ArrayList<String>> dataEntriesIds; //holds DataCollectionEntry IDs for each skill
    private final ArrayList<String> skillsList;
    private final ArrayList<String> entryID; //holds unique DataCollectionEntry ID
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

    public KeywordAnalyser(CollectionDataModel collectionDataModel, KeywordDataModel keywordDataModel, ObservableList<ArrayList<String>> skillCombinations) {
        this.collectionDataModel = collectionDataModel;
        this.resultDataModel = new CollectionDataModel();
        this.keywordDataModel = keywordDataModel;
        this.stats = FXCollections.observableArrayList();
        this.dataEntries = new ArrayList<>();
        this.dataEntriesIds = new ArrayList<>();
        this.skillCombinations = skillCombinations;
        this.skillsList = new ArrayList<>();
        this.entryID = new ArrayList<>();
    }
    public ObservableList<SkillStat> analyse() {
        // For each DataCollection (ex. a search result or file)
        for(DataCollection collection : collectionDataModel.getDataCollections()) {
            resultSource = resultSource + " " + collection.title();
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
                                        dataEntries.get(index).add(entry);
                                        dataEntriesIds.get(index).add(entry.id());
                                        skillAdded = true;
                                        break;
                                    }
                                }
                                // If skill is not present create new entry
                                if(!skillAdded) {
                                    stats.add(newStat);
                                    ArrayList<DataCollectionEntry> entries = new ArrayList<>();
                                    ArrayList<String> entryIds = new ArrayList<>();
                                    entries.add(entry);
                                    entryIds.add(entry.id());
                                    dataEntries.add(entries);
                                    dataEntriesIds.add(entryIds);
                                    skillsList.add(newStat.skill());
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
            String source = resultSource + " with skill " + stat.skill();
            resultDataModel.addDataCollection(new DataCollection(stat.skill(), dataEntries.get(index), source , java.time.LocalDate.now().toString()));
        }
        analyzeSkillCombination();
        return stats;
    }

    private void analyzeSkillCombination(){
        ArrayList<ArrayList<String>> listsToCompare = new ArrayList<>();
        for(ArrayList<String> comb: skillCombinations){
            if(containValidSkills(comb)){
                StringBuilder skillsString = new StringBuilder();
                for(String s: comb){
                    int index = skillsList.indexOf(s);
                    listsToCompare.add(dataEntriesIds.get(index));
                    skillsString.append(s).append(", ");
                }
                skillsString.delete(skillsString.length()-2, skillsString.length()-1);
                //ArrayList<String> commonEntries ;
                List<String> commonEntries = new ArrayList<>(listsToCompare.get(0));
                for(int i= 1; i<listsToCompare.size(); i++){
                    //commonEntries.removeAll(listsToCompare.get(i));
                    commonEntries = commonEntries.stream().filter(listsToCompare.get(i)::contains).collect(Collectors.toList());
                }
                int count = commonEntries.size();
                float percentage = (float) count / entryID.size() * 100;
                stats.add(new SkillStat(skillsString.toString(), count, percentage));
                int index = skillsList.indexOf(comb.get(0));
                ArrayList<DataCollectionEntry> comboDataEntries = new ArrayList<>();
                for(DataCollectionEntry entry: dataEntries.get(index)){
                    if(commonEntries.contains(entry.id())){
                        comboDataEntries.add(entry);
                    }
                }
                dataEntries.add(comboDataEntries);
                resultDataModel.addDataCollection(new DataCollection(skillsString.toString(), comboDataEntries, "" , java.time.LocalDate.now().toString()));
            }
        }
    }

    private boolean containValidSkills(ArrayList<String> skills){
        for(String s: skills){
            if(!skillsList.contains(s)){
                return false;
            }
        }
        return true;
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

    public CollectionDataModel getResultDataModel(){
        return resultDataModel;
    }

    public String getCollectionNames(){
        return resultSource;
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
