package CallCenter;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

enum Level{
    OPERATOR(1), SUPERVISOR(2), DIRECTOR(3);
    private int level;
    Level(int level){
        this.level = level;
    }
    int getLevel(){
        return level;
    }
    static Level getNextLevel(Level prevLevel){
        for(Level val : values()){
            if(val.level == prevLevel.level + 1){
                return val;
            }
        }
        return null;
    }
}
enum CallStatus {
    NOT_PICKED, IN_PROGRESS, ESCALATED, COMPLETED
}
enum CallPriority{
    LOW, MID, HIGH
}
interface Employee{
    Integer getId();
    boolean isFree();
    Level getLevel();
    boolean handleCall(Call call) throws InterruptedException;
}
interface Call{
    Integer getCallId();
    Integer getTime();
    CallStatus getStatus();
    void updateStatus(CallStatus callstatus);
    boolean isCompleted();
    Level getLevel();
    void setLevel(Level level);
    CallPriority getPriority();
}
interface CallQueue{
    Call getNextCall();
    void pushInQueue(Call call);
}
interface EmployeeQueue{
    Employee getNextEmployee(Level level);
}
interface CallDispatcher {
    void dispatchCall() throws InterruptedException;
}


class MyEmployee implements Employee{
    int id;
    boolean free;
    Level level;
    MyEmployee(Integer id, Level level) {
        free = true;
        this.level = level;
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isFree() {
        return free;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public boolean handleCall(Call call) throws InterruptedException{
        this.free = false;
        boolean canHandle = true;
        call.updateStatus(CallStatus.IN_PROGRESS);
        System.out.printf("Employees %s is busy with call %s\n", this.getId(), call.getCallId());
        System.out.printf("Call %s is in status %s\n", call.getCallId(), call.getStatus());
        Thread.sleep(5 * 1000);
        int randomValue = new Random(System.currentTimeMillis()).nextInt(100);
        canHandle = randomValue > 50;
        if(!canHandle){
            this.free = true;
            return false;
        }
        call.updateStatus(CallStatus.COMPLETED);
        this.free = true;
        System.out.printf("Employees %s is free now\n", this.getId());
        return true;
    }
}

class MyCall implements Call{
    Integer callId;
    Integer time;
    CallStatus status;
    Level level;
    CallPriority priority;
    public MyCall(Integer callId, CallPriority priority, Integer time) {
        level = Level.OPERATOR;
        status = CallStatus.NOT_PICKED;
        this.callId = callId;
        this.priority = priority;
        this.time = time;
    }

    @Override
    public Integer getCallId() {
        return callId;
    }

    @Override
    public Integer getTime() {
        return time;
    }

    @Override
    public CallStatus getStatus() {
        return status;
    }

    @Override
    public void updateStatus(CallStatus callstatus) {
        this.status = callstatus;
    }

    @Override
    public boolean isCompleted() {
        return CallStatus.COMPLETED.equals(status);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public CallPriority getPriority() {
        return priority;
    }
}
class CallQueueComparator implements Comparator<Call>{
    @Override
    public int compare(Call call1, Call call2) {
        if(call1.getPriority().ordinal() < call2.getPriority().ordinal()){ //for descending order
            return 1;
        }else if(call1.getPriority().ordinal() > call2.getPriority().ordinal()){
            return -1;
        }if(call1.getStatus().ordinal() < call2.getStatus().ordinal()){ //for descending order
            return 1;
        }else if(call1.getStatus().ordinal() > call2.getStatus().ordinal()){
            return -1;
        }else if(call1.getTime() > call2.getTime()){ //for ascending order
            return 1;
        }else if(call1.getTime() < call2.getTime()){
            return -1;
        }else{
            return 0;
        }
    }
}
class MyCallQueue implements CallQueue{
    private Queue<Call> queue;
    public MyCallQueue() {
        queue = new PriorityBlockingQueue<Call>(10, new CallQueueComparator());
        queue.add(new MyCall(1, CallPriority.HIGH, 1));
        queue.add(new MyCall(2, CallPriority.LOW, 2));
        queue.add(new MyCall(3, CallPriority.MID, 3));
        queue.add(new MyCall(4, CallPriority.LOW, 4));
        queue.add(new MyCall(5, CallPriority.HIGH, 5));

        for(Call call : queue){
            System.out.printf("CallId: %s, priority: %s , time: %s\n", call.getCallId(), call.getPriority(), call.getTime());
        }
    }

    @Override
    public Call getNextCall() {
        if(queue.isEmpty()){
            return null;
        }
        return queue.poll();
    }

    @Override
    public void pushInQueue(Call call) {
        queue.add(call);
    }
}

class MyEmployeeQueue implements EmployeeQueue{
    Map<Integer, Queue<Employee> > employeeQueue;
    public MyEmployeeQueue() {
        employeeQueue = new HashMap<Integer, Queue<Employee>>();

        Queue<Employee> level1Q = new LinkedList<Employee>();
        level1Q.add(new MyEmployee(1, Level.OPERATOR));
        level1Q.add(new MyEmployee(2, Level.OPERATOR));
        employeeQueue.put(Level.OPERATOR.getLevel(), level1Q);

        Queue<Employee> level2Q = new LinkedList<Employee>();
        level2Q.add(new MyEmployee(3, Level.SUPERVISOR));
        level2Q.add(new MyEmployee(4, Level.SUPERVISOR));
        employeeQueue.put(Level.SUPERVISOR.getLevel(), level2Q);

        Queue<Employee> level3Q = new LinkedList<Employee>();
        level3Q.add(new MyEmployee(5, Level.DIRECTOR));
        level3Q.add(new MyEmployee(6, Level.DIRECTOR));
        employeeQueue.put(Level.DIRECTOR.getLevel(), level3Q);

        for(Level level : Level.values()){
            if(!employeeQueue.containsKey(level.getLevel())) continue;
            for(Employee employee : employeeQueue.get(level.getLevel())){
                System.out.printf("EmployeeId: %s, level: %s\n", employee.getId(), employee.getLevel());
            }
        }
    }

    @Override
    public Employee getNextEmployee(Level level) {
        if(level == null || !employeeQueue.containsKey(level.getLevel())){
            return null;
        }
        Employee employeeFreeToPickCall = null;
        synchronized (level){
            for(Employee employee : employeeQueue.get(level.getLevel())){
                if(employee.isFree()){
                    employeeFreeToPickCall = employee;
                    break;
                }
            }
        }
        return employeeFreeToPickCall;
    }
}

class MyCallDispatcher implements CallDispatcher{
    CallQueue callQueue;
    EmployeeQueue employeeQueue;
    ThreadPoolExecutor executor;

    MyCallDispatcher(){
        callQueue = new MyCallQueue();
        employeeQueue = new MyEmployeeQueue();
    }

    @Override
    public void dispatchCall() throws InterruptedException {
        Call nextCall = callQueue.getNextCall();
        if(nextCall == null){
            System.out.println("No calls left in the system");
            throw new InterruptedException("All calls processed");
        }
        Employee employee = this.employeeQueue.getNextEmployee(nextCall.getLevel());
        if(employee == null){
            System.out.printf("All employees of level %s is occupied, cannot pick call %s\n", nextCall.getLevel(), nextCall.getCallId());
            callQueue.pushInQueue(nextCall);
            return;
        }
        boolean success = employee.handleCall(nextCall);
        if (!success) escalate(nextCall);
        System.out.printf("Call %s is in status %s\n", nextCall.getCallId(), nextCall.getStatus());
    }

    private void escalate(Call call) {
        System.out.printf("Escalating the call %s\n", call.getCallId());
        call.setLevel(Level.getNextLevel(call.getLevel()));
        call.updateStatus(CallStatus.ESCALATED);
        this.callQueue.pushInQueue(call);
    }

}

public class CallCenter{
    public static void main(String[] args) {
        CallDispatcher callDispatcher = new MyCallDispatcher();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        AtomicBoolean run = new AtomicBoolean(true);
        System.out.println("Processing begins!!");
        while (run.get()){
            try{
                executorService.submit(() -> {
                    try{
                        callDispatcher.dispatchCall();
                    } catch (InterruptedException exception){
                        run.set(false);
                        System.out.printf("InterruptedException occurred %s\n", exception.getMessage());
                    } catch (Exception exception){
                        run.set(false);
                        System.out.printf("Exception occurred %s\n", exception);
                        exception.printStackTrace();
                    }
                });
                Thread.sleep(2 * 1000);
            } catch (InterruptedException exception){
                System.out.printf("InterruptedException occurred in main %s\n", exception);
            }
        }
        awaitTerminationAfterShutdown(executorService);
        System.out.println("Processing completed!!");
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
