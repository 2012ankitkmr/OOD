package RateLimiter;
/***
 * Create rate limiter for 10 request per minute
 *
 */

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class UserInfo{
    private Long userId;
    private Queue<Long> timestamps;
    private HashMap<Long, Integer> requestCountMap;

    public UserInfo(Long userId) {
        this.userId = userId;
        timestamps = new LinkedList<>();
        requestCountMap = new HashMap<>();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Queue<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(Queue<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public HashMap<Long, Integer> getRequestCountMap() {
        return requestCountMap;
    }

    public void setRequestCountMap(HashMap<Long, Integer> requestCountMap) {
        this.requestCountMap = requestCountMap;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", requestCountMap=" + requestCountMap.entrySet().stream().map(d -> String.format("%s: %s", getHumanReadableTime(d.getKey()), d.getValue())).collect(Collectors.joining(", ")) +
                '}';
    }

    private static String getHumanReadableTime(long timeInSecond){
        return (new SimpleDateFormat("mm:ss")).format(new Date(timeInSecond * 1000));
    }
}

class Database{
    private static final HashMap<Long, UserInfo> users;
    static {
        users = new HashMap<Long, UserInfo>(){{
            put(1L, new UserInfo(1L));
            put(2L, new UserInfo(2L));
        }};
    }
    static UserInfo getUserInfo(long userId){
        return users.get(userId);
    }
}
class RateLimiter{
    /**
     * Implementation of sliding window counter algorithm
     */
    private static final int MAX_REQ_ALLOWED = 3;
    private static final int WINDOW_SIZE = 10;
    private static ConcurrentHashMap<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public static void acquire(Long userId) throws Exception{
        locks.putIfAbsent(userId, new ReentrantLock());
        ReentrantLock lock = locks.get(userId);
        System.out.printf("Thread %s : Waiting for acquiring lock for userid %s\n", Thread.currentThread().getName(), userId);
        lock.tryLock(6, TimeUnit.SECONDS); //Try for 4 second
        System.out.printf("Thread %s : Acquired lock for userid %s\n", Thread.currentThread().getName(), userId);
        try{
            UserInfo userInfo = Database.getUserInfo(userId);
            evictObsoleteEntries(userInfo);
            int prevReqCount = userInfo.getRequestCountMap().entrySet().stream().map(Map.Entry::getValue).mapToInt(Integer::intValue).sum();
            if(prevReqCount + 1 > MAX_REQ_ALLOWED){
                System.out.printf("Thread %s : current userInfo: %s\n", Thread.currentThread().getName(), userInfo);;
                throw new Exception("Rate limit exceeded");
            }
            Long currTS = System.currentTimeMillis() / 1000; //In second
            System.out.printf("Thread %s : Adding time %s for user %s\n", Thread.currentThread().getName(), getHumanReadableTime(currTS), userId);
            userInfo.getTimestamps().add(currTS);
            if(userInfo.getRequestCountMap().containsKey(currTS)){
                userInfo.getRequestCountMap().put(currTS,
                        userInfo.getRequestCountMap().get(currTS) + 1);
            }else{
                userInfo.getRequestCountMap().put(currTS, 1);
            }
            System.out.printf("Thread %s : updated userInfo: %s\n", Thread.currentThread().getName(), userInfo);
        } finally {
            System.out.printf("Thread %s : Releasing lock for userid %s\n", Thread.currentThread().getName(), userId);
            lock.unlock();
        }
    }

    public static void evictObsoleteEntries(UserInfo userInfo){
        long currTS = System.currentTimeMillis() / 1000;
        Queue<Long> timestamps = userInfo.getTimestamps();
        while (!timestamps.isEmpty() && (currTS - timestamps.peek()) > WINDOW_SIZE){
            System.out.printf("Removing timestamp for user %s current time %s removed time %s and diff %s\n", userInfo.getUserId(), getHumanReadableTime(currTS), getHumanReadableTime(timestamps.peek()), (currTS - timestamps.peek()));
            userInfo.getRequestCountMap().remove(timestamps.peek());
            timestamps.poll();
        }
    }

    private static String getHumanReadableTime(long timeInSecond){
        return "\u001B[32m" + (new SimpleDateFormat("mm:ss")).format(new Date(timeInSecond * 1000)) + "\u001B[0m";
    }
}
class CustomRunnable implements Runnable{
    private final long userId;
    public final String ANSI_BLUE = "\u001B[34m";
    public final String ANSI_RESET = "\u001B[0m";
    public final String ANSI_GREEN = "\u001B[32m";
    public CustomRunnable(long userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++){
            try {
                System.out.printf("Thread %s : API hit %s from userid %s\n", Thread.currentThread().getName(), (i+1), userId);
                RateLimiter.acquire(userId);
                System.out.printf("Thread %s : API hit %s from userid %s %s processing....... %s \n", Thread.currentThread().getName(), (i+1), userId, ANSI_GREEN, ANSI_RESET);
            } catch (Exception ex){
                System.out.printf("Thread %s : API hit %s Exception occurred %s%s%s\n", Thread.currentThread().getName(), (i+1), ANSI_BLUE, ex.getMessage(), ANSI_RESET);
            }
            try {
                Thread.sleep((new Random().nextInt(6)+1) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
public class Main {
    public static void main(String[] args) throws Exception {
        Thread t11 = new Thread(new CustomRunnable(1), "T11");
        Thread t12 = new Thread(new CustomRunnable(1), "T12");
        Thread t13 = new Thread(new CustomRunnable(1), "T13");
        Thread t14 = new Thread(new CustomRunnable(1), "T14");
        Thread t15 = new Thread(new CustomRunnable(1), "T15");
        Thread t16 = new Thread(new CustomRunnable(1), "T16");
        Thread t21 = new Thread(new CustomRunnable(2), "T21");
        t11.start();
        t12.start();
        t13.start();
        t14.start();
        t15.start();
        t16.start();
        t21.start();

        t11.join();
        t12.join();
        t13.join();
        t14.join();
        t15.join();
        t16.join();
        t21.join();
        System.out.println("All threads processed");
    }
}
