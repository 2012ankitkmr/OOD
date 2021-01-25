package TinyURL;

import com.sun.xml.internal.rngom.parse.host.Base;
import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class RandomIdGenerator {
    private static final long CUSTOM_EPOCH = 1611499933000L; //Jan 1 2020
    private static final int MAX_TIME_STAMP_BITS=41; //can cover 70 years
    private static final int MAX_NODE_ID_BITS=10; //
    private static final int MAX_SEQUENCE_BITS=12;
    private static final int MAX_NODE_ID_VALUE = (int) Math.pow(2, MAX_NODE_ID_BITS)-1;
    private static final int MAX_SEQUENCE_VALUE = (int) Math.pow(2, MAX_SEQUENCE_BITS)-1;
    private final int nodeId;
    private volatile int lastSequence = 0;
    private volatile long lastTimeStamp = 0L;

    RandomIdGenerator(int nodeId){
        this.nodeId = nodeId;
    }

    public synchronized long getNextId(){
        long nextEpoch = System.currentTimeMillis() - CUSTOM_EPOCH;
        if(nextEpoch == lastTimeStamp){
            lastSequence = (lastSequence + 1) % MAX_SEQUENCE_VALUE;
            System.out.printf("Thread %s generated new sequence %s\n", Thread.currentThread().getName(), lastSequence);
            if(lastSequence == 0){ //All sequence exhausted
                System.out.printf("Thread %s All sequence exhausted\n", Thread.currentThread().getName());
                nextEpoch = getNextEpoch();
            }
        }else {
            lastSequence = 0;
        }
        lastTimeStamp = nextEpoch;
        long id = (nextEpoch << (MAX_NODE_ID_BITS + MAX_SEQUENCE_BITS));
        id = id | (nodeId << MAX_SEQUENCE_BITS);
        id = id | lastSequence;
        return id;
    }

    private long getNextEpoch(){
        long currTimeStamp = System.currentTimeMillis();
        if(lastTimeStamp == 0L){
            return currTimeStamp - CUSTOM_EPOCH;
        }
        while (currTimeStamp == lastTimeStamp){
            currTimeStamp = System.currentTimeMillis();
        }
        return currTimeStamp - CUSTOM_EPOCH;
    }
}
class Database{
    private static Map<Long, String> urls = new HashMap<>();
    static void addUrl(Long id, String url){
        urls.put(id, url);
    }
    static String getUrl(Long id){
        return urls.getOrDefault(id, null);
    }
    static void print(){
        System.out.println("\n----------Database Entries-------------");
        urls.forEach((key, value) -> System.out.printf("id %s, url %s\n", key, value));
        System.out.println("------------------------------------\n");
    }
}
class TinyURL{
    private final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    RandomIdGenerator randomIdGenerator = new RandomIdGenerator(1);

    public String pushUrl(String url){
        String shortenUrl = null;
        int retry = 10;
        long uniqueId = randomIdGenerator.getNextId();
        while (retry-- > 0){
            if(Database.getUrl(uniqueId) == null) break;
            uniqueId = randomIdGenerator.getNextId();
        }
        Database.addUrl(uniqueId, url);
        System.out.printf("Unique id generated %s for url %s\n", uniqueId, url);
        shortenUrl = encodeUrl(uniqueId);
        return shortenUrl;
    }
    public String getUrl(String shortenUrl){
        long id = decodeUrl(shortenUrl);
        System.out.printf("Get request for id %s\n", id);
        return Database.getUrl(id);
    }

    private String encodeUrl(long id){
        List<Character> chars = new ArrayList<>();
        while (id > 0){
            int index = (int) (id % BASE64_CHARS.length());
            chars.add(BASE64_CHARS.charAt(index));
            id = id / BASE64_CHARS.length();
        }
        Collections.reverse(chars);
        return chars.stream().map(Object::toString).
                collect(Collectors.joining(""));
    }

    private long decodeUrl(String shortenUrl){
        long id = 0;
        for (int i = 0; i < shortenUrl.length(); i++) {
            id = id * BASE64_CHARS.length();
            int index = BASE64_CHARS.indexOf(shortenUrl.charAt(i));
            id += index;
        }
        return id;
    }
}
public class Main {
    private static Thread createThread(Queue<Long> queue, RandomIdGenerator randomIdGenerator, String threadName){
        return new Thread(() -> {
            for(int i=0; i<10; i++){
                long id = randomIdGenerator.getNextId();
                queue.add(id);
                System.out.printf("Thread %s id: %s\n", Thread.currentThread().getName(), id);
            }
        });
    }
    static void uniqueIdGeneratorTesting(){
        Queue<Long> queue = new LinkedList<>();
        RandomIdGenerator randomIdGenerator = new RandomIdGenerator(1);
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for(int i=0; i<10; i++){
            threadPool.submit(createThread(queue, randomIdGenerator, "T" + (i+1)));
        }
        threadPool.shutdown();
        try {
            if(!threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> idList = new ArrayList<>(queue);
        idList.sort(Comparator.naturalOrder());
        System.out.println(queue.stream().map(String::valueOf).collect(Collectors.joining("\n")));
        System.out.printf("IDS: %s Unique: %s\n", idList.size(), new HashSet(idList).size());
    }

    public static void main(String[] args) {
//        uniqueIdGeneratorTesting();
        TinyURL tinyURL = new TinyURL();
        String url1 = tinyURL.pushUrl("https://www.example.com");
        System.out.printf("Shorten Url '%s'\n", url1);
        System.out.printf("Actual Url '%s'\n", tinyURL.getUrl(url1));

        String url2 = tinyURL.pushUrl("https://www.abc.com");
        System.out.printf("Shorten Url '%s'\n", url2);
        System.out.printf("Actual Url '%s'\n", tinyURL.getUrl(url2));

//        Database.print();
    }
}
