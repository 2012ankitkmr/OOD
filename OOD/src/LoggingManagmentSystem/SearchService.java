package LoggingManagmentSystem;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

class LookUpResult{
    private String query;
    private List<Appearance> appearances;

    public LookUpResult(String query, List<Appearance> appearances) {
        this.query = query;
        this.appearances = appearances;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Appearance> getAppearances() {
        return appearances;
    }

    public void setAppearances(List<Appearance> appearances) {
        this.appearances = appearances;
    }

    @Override
    public String toString() {
        return String.format("Query: %s, Result: { %s }", query, String.join(", ", appearances.toString()));
    }
}

interface Indexer{
    void process(List<Document> documents);
    List<LookUpResult> lookUpQuery(List<String> queryList);
    Map<String, List<Appearance>> getAllIndex();
}

interface Database{
    Document getDocument(Integer docId);
    void saveDocument(List<Document> documents);
}
enum LogLevel{
    INFO, DEBUG, ERROR;

    static LogLevel getByOrdinal(Integer ordinal){
        for(LogLevel level : values()){
            if(level.ordinal() == ordinal){
                return level;
            }
        }
        return null;
    }
}

class Document<T>{
    private Integer id;
    private T data;

    public Document(T data) {
        this.id = new Random().nextInt(100000);
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
class Log{
    LogLevel logLevel;
    Long time;
    String message;

    public Log(LogLevel logLevel, String message) {
        this.logLevel = logLevel;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class Appearance{
    private Integer documentId;
    private Integer frequency;

    public Appearance(Integer documentId, Integer frequency) {
        this.documentId = documentId;
        this.frequency = frequency;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return String.format("[DocumentId %s, Frequency %s]", documentId, frequency);
    }
}

class AppearanceComparator implements Comparator<Appearance>{
    @Override
    public int compare(Appearance o1, Appearance o2) {
        if(o1.getFrequency() > o2.getFrequency()){
            return -1;
        } else if(o1.getFrequency() < o2.getFrequency()){
            return 1;
        } else {
            return 0;
        }
    }
}

class InvertedIndexAlgo implements Indexer{
    private String separator = " ";
    private String excludedPattern = "[\\., \\,]";
    private Map<String, List<Appearance>> indexes;

    InvertedIndexAlgo(String separator){
        indexes = new HashMap<>();
        this.separator = separator;
    }

    @Override
    public void process(List<Document> documents) {
        Map<String, List<Appearance>> termAppearanceMap = new HashMap<>();
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        for(Document document : documents){
            List<String> allTerms = getTerms(document.getData());
            allTerms.forEach(term -> {
                Integer count = termFrequencyMap.getOrDefault(term, 0);
                count += 1;
                termFrequencyMap.put(term, count);
            });
            termFrequencyMap.keySet().forEach(term -> {
                Appearance appearance = new Appearance(document.getId(),
                        termFrequencyMap.getOrDefault(term, 0) + 1);
                List<Appearance> appearances = termAppearanceMap.getOrDefault(term, new ArrayList<>());
                appearances.add(appearance);
                termAppearanceMap.put(term, appearances);
            });
        }
        updateIndexes(termAppearanceMap);
    }

    private List<String> getTerms(Object data){
        List<String> allTerms = Collections.emptyList();
        if(data instanceof String){
            allTerms = Arrays.stream(((String)data).split(separator)).
                    map(String::trim).map(t -> t.replaceAll(excludedPattern, "")).filter(t -> t.length() > 0).collect(Collectors.toList());
        }
        return allTerms;
    }

    private void updateIndexes(Map<String, List<Appearance>> termAppearanceMap){
        termAppearanceMap.keySet().forEach(term -> {
            List<Appearance> indexedAppearances = indexes.getOrDefault(term, new ArrayList<>());
            indexedAppearances.addAll(termAppearanceMap.getOrDefault(term, Collections.emptyList()));
            indexes.put(term, indexedAppearances);
        });
    }

    public List<LookUpResult> lookUpQuery(List<String> queryList){
        List<LookUpResult> results = new ArrayList<>();
        for(String query : queryList){
            LookUpResult lookUpResult = new LookUpResult(query, indexes.getOrDefault(query, Collections.emptyList()));
            lookUpResult.getAppearances().sort(new AppearanceComparator());
            results.add(lookUpResult);
        }
        return results;
    }

    @Override
    public Map<String, List<Appearance>> getAllIndex() {
        return indexes;
    }
}
class CustomInMemoryDatabase implements Database{
    private List<Document> allDocuments = new ArrayList<>();

    CustomInMemoryDatabase(){ }

    CustomInMemoryDatabase(List<Document> documents){
        allDocuments.addAll(documents);
    }

    @Override
    public Document getDocument(Integer docId) {
        return allDocuments.stream().filter(
                document -> document.getId().equals(docId)
        ).findAny().orElse(null);
    }

    @Override
    public void saveDocument(List<Document> documents) {
        documents.forEach( document -> {
            Document existingDocument = getDocument(document.getId());
            if(existingDocument != null){
                existingDocument.setData(document.getData());
            }else{
                allDocuments.add(document);
            }
        });
    }
}
class SearchService{
    private Database database;
    private Indexer indexer;
    public final String ANSI_GREEN = "\u001B[32m";
    public final String ANSI_BLUE = "\u001B[34m";
    public final String ANSI_RESET = "\u001B[0m";
    SearchService() {
        database = new CustomInMemoryDatabase();
        indexer = new InvertedIndexAlgo(" ");
    }

    public void outputAllIndexes(){
        System.out.println("\n\n------------Indexes--------------");
        Map<String, List<Appearance>> allIndexes = indexer.getAllIndex();
        allIndexes.forEach(
                (key, value) ->
                        System.out.printf("For Term: %s, Results: %s\n", key, String.join(", ", value.toString()))
        );
        System.out.printf("Total Indexes: %s\n", allIndexes.size());
        System.out.println("----------------------------------\n\n");
    }

    public void uploadDocuments(List<Document> documents){
        indexer.process(documents);
        database.saveDocument(documents);
    }

    public void outputQueryResults(List<String> queryList){
        List<LookUpResult> results = indexer.lookUpQuery(queryList);
        for(LookUpResult data : results){
            System.out.printf("Search Query: %s%s%s\n", ANSI_GREEN, data.getQuery(), ANSI_RESET);
            List<Appearance> appearances = data.getAppearances();
            if(appearances == null || appearances.size() == 0){
                System.out.println("\t\tNo result found\n");
                continue;
            }
            appearances.forEach(appearance -> {
                Document document = database.getDocument(appearance.getDocumentId());
                String highlightedData = ((String) document.getData()).replaceAll(
                        data.getQuery(),
                        String.format("%s%s%s", ANSI_BLUE, data.getQuery(), ANSI_RESET)
                );
                System.out.printf("\t\t Document %s : %s\n", document.getId(), highlightedData);
            });
            System.out.println();
        }
    }
}
class LogQueueComparator implements Comparator<Log>{
    @Override
    public int compare(Log o1, Log o2) {
        if(o1.getLogLevel() != o2.getLogLevel()){
            return o1.getLogLevel().compareTo(o2.getLogLevel());
        }
        return o1.getTime().compareTo(o2.getTime());
    }
}
class LoggerQueue{
    Queue<Log> queue;
    LoggerQueue() {
        queue = new PriorityBlockingQueue(10, new LogQueueComparator());
    }
    public void pushLog(Log log){
        queue.add(log);
    }
    public Log getLog(){
        if(queue.isEmpty()) return null;
        return queue.poll();
    }
    public boolean isLogAvailable(){
        return !queue.isEmpty();
    }
}
class LoggerClient{
    LoggerQueue loggerQueue;
    LoggerClient(LoggerQueue loggerQueue){
        this.loggerQueue = loggerQueue;
    }
    public void pushLog(LogLevel level, String message) {
        Log log = new Log(level, message.trim());
        loggerQueue.pushLog(log);
    }
}
class LoggerServer extends Thread{
    LoggerQueue loggerQueue;
    SearchService searchService;
    LoggerServer(LoggerQueue loggerQueue, SearchService searchService){
        this.loggerQueue = loggerQueue;
        this.searchService = searchService;
    }
    @Override
    public void run() {
//        System.out.println("started polling logs");
        try{
            while (true){
                if(loggerQueue.isLogAvailable()){
                    Log log = loggerQueue.getLog();
                    Document document = new Document(log.getMessage());
                    searchService.uploadDocuments(Collections.singletonList(document));
                }
                Thread.sleep(3 * 1000);
            }
        } catch (Exception exception){
            System.out.printf("Error occurred while polling logs %s\n", exception.getMessage());
        } finally {
            //close all connections
        }
        System.out.println("Exiting from log polling....");
    }
}

