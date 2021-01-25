package VendingMachine;

import java.util.*;

enum ItemType{
    COLD_DRINK, BISCUIT, CHOCOLATE
}
interface Item{
    Integer getId();
    Double getAmount();
    //Can add purchase date & date of expiry and so on...
}

class ColdDrink implements Item{
    private Integer id;
    private Double amount;

    public ColdDrink(Integer id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Double getAmount() {
        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
class Biscuit implements Item{
    private Integer id;
    private Double amount;

    public Biscuit(Integer id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Double getAmount() {
        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
class Chocolate implements Item{
    private Integer id;
    private Double amount;

    public Chocolate(Integer id, Double amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Double getAmount() {
        return null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

class Inventory{
    static Map<ItemType, Queue<Item>> records;
    static Map<ItemType, Double> itemPriceMap;
    static {
        itemPriceMap = new HashMap(){{
           put(ItemType.COLD_DRINK, 100.0);
           put(ItemType.BISCUIT, 120.0);
           put(ItemType.CHOCOLATE, 150.0);
        }};
        records = new HashMap(){{
            put(ItemType.COLD_DRINK, new LinkedList(){{
                add(new ColdDrink(1, itemPriceMap.get(ItemType.COLD_DRINK)));
                add(new ColdDrink(2, itemPriceMap.get(ItemType.COLD_DRINK)));
            }});
            put(ItemType.BISCUIT, new LinkedList(){{
                add(new Biscuit(3, itemPriceMap.get(ItemType.BISCUIT)));
                add(new Biscuit(4, itemPriceMap.get(ItemType.BISCUIT)));
            }});
            put(ItemType.CHOCOLATE, new LinkedList(){{
                add(new Chocolate(5, itemPriceMap.get(ItemType.CHOCOLATE)));
            }});
        }};
    }
    static List<Item> getItem(ItemType type, Integer count) {
        if(!isItemAvailable(type)){ return Collections.emptyList(); }
        List<Item> items = new ArrayList();
        for (int i = 0; i < count; i++) {
            items.add(records.get(type).poll());
        }
        return items;
    }

    static Double getItemPrice(ItemType type) {
        return itemPriceMap.get(type);
    }

    static boolean isItemAvailable(ItemType type) {
        if(!records.containsKey(type)) return false;
        return records.get(type).size() > 0;
    }
    static Integer totalAvailableInStock(ItemType type){
        if(isItemAvailable(type)){
            return records.get(type).size();
        }
        return 0;
    }
}

enum RequestStatus{
    NEW, AMOUNT_PAID, DISPENSED, CANCELLED, AMOUNT_RETURNED, OUT_OF_STOCK
}
class Request{
    private Integer id;
    private RequestStatus status;
    private ItemType itemType;
    private Integer itemCount;
    private Double amountReceived;
    boolean isCompleted;

    public Request(ItemType itemType) {
        this.id = new Random().nextInt();
        this.itemType = itemType;
        this.status = RequestStatus.NEW;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Double getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(Double amountReceived) {
        this.amountReceived = amountReceived;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getItemName(){
        return itemType.name().replaceAll("_", " ");
    }
}
interface RequestStatusHandler{
    void handle(Request request)  throws Exception;
}
class RequestNewStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception{
        boolean isAvailable = Inventory.isItemAvailable(request.getItemType());
        if(!isAvailable){
            System.out.println("Sorry for the inconvenience, currently we are out of stock!!");
            request.setStatus(RequestStatus.OUT_OF_STOCK);
            RequestStatusController.process(request);
            return;
        }
        Double itemPrice = Inventory.getItemPrice(request.getItemType());
        System.out.printf("Item price is %s\n", itemPrice);
        System.out.print("Want to continue enter 1...  ");
        int wantToContinue = Main.scanner.nextInt();
        if(wantToContinue == 1){
            Integer availableStock = Inventory.totalAvailableInStock(request.getItemType());
            System.out.printf("Total available %s\n", availableStock);
            System.out.print("Please enter quantity => ");
            request.setItemCount(Main.scanner.nextInt());
            while(request.getItemCount() > availableStock){
                System.out.printf("Please enter quantity maximum %s => ", availableStock);
                request.setItemCount(Main.scanner.nextInt());
            }
            Double amountToCollect = (itemPrice * request.getItemCount());
            System.out.printf("Please insert amount %s => ", amountToCollect);
            request.setAmountReceived(Main.scanner.nextDouble());
            while (request.getAmountReceived() < amountToCollect){
                System.out.printf("Please insert amount %s to proceed => ", amountToCollect - request.getAmountReceived());
                request.setAmountReceived(request.getAmountReceived() + Main.scanner.nextDouble());
            }
            System.out.printf("Amount %s collected successfully for the item %s(quantity=%s)\n",
                    request.getAmountReceived(), request.getItemName(), request.getItemCount());
            request.setStatus(RequestStatus.AMOUNT_PAID);
            RequestStatusController.process(request);
        }
    }
}
class RequestAmountPaidStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception {
        System.out.println("Want to continue enter 1...");
        int wantToContinue = Main.scanner.nextInt();
        if(wantToContinue != 1){
            request.setStatus(RequestStatus.CANCELLED);
            RequestStatusController.process(request);
            return;
        }
        System.out.println("Dispensing item ...");
        Inventory.getItem(request.getItemType(), request.getItemCount());
        Thread.sleep(2 * 1000);
        System.out.printf("Item %s has been dispensed successfully\n", request.getItemName());
        request.setStatus(RequestStatus.DISPENSED);
        RequestStatusController.process(request);
    }
}
class RequestDispensedStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception {
        System.out.println("Thanks for the shopping with us, Good Bye!!");
        //Can add another state like review & rating
    }
}
class RequestCancelledStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception {
        System.out.println("Cancelling the request ...");
        Thread.sleep(1 * 1000);
        System.out.println("Your request cancelled successfully!!");
        if(request.getAmountReceived() > 0){
            request.setStatus(RequestStatus.AMOUNT_RETURNED);
            RequestStatusController.process(request);
        }
    }
}
class RequestAmountReturnedStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception {
        System.out.println("Please collect your amount");
        Thread.sleep(1 * 1000);
        System.out.println("Amount returned successfully");
    }
}
class RequestOutOfStockStatusHandler implements RequestStatusHandler{
    @Override
    public void handle(Request request) throws Exception {
        System.out.println("Sending refill request...");
        Thread.sleep(1 * 100);
        System.out.printf("Refill request has sent successfully, " +
                "the item %s will be available with in 1 hour\n", request.getItemName());
    }
}

class RequestStatusController {
    static Map<RequestStatus, RequestStatusHandler> requestStatusHandlerMap;
    static {
        requestStatusHandlerMap = new HashMap(){{
            put(RequestStatus.NEW, new RequestNewStatusHandler());
            put(RequestStatus.AMOUNT_PAID, new RequestAmountPaidStatusHandler());
            put(RequestStatus.DISPENSED, new RequestDispensedStatusHandler());
            put(RequestStatus.CANCELLED, new RequestCancelledStatusHandler());
            put(RequestStatus.AMOUNT_RETURNED, new RequestAmountReturnedStatusHandler());
            put(RequestStatus.OUT_OF_STOCK, new RequestOutOfStockStatusHandler());
        }};
    }

    static void process(Request request) throws Exception{
        if(!requestStatusHandlerMap.containsKey(request.getStatus())){
            throw new Exception("Cannot handle request status");
        }
        requestStatusHandlerMap.get(request.getStatus()).handle(request);
    }
}

public class Main{
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        try {
            Integer input;
            while (true){
                System.out.printf("-----Menu---\n%s\n%s\n%s\n",
                        "Enter 1 for Cold Drink",
                        "Enter 2 for Biscuit",
                        "Enter 3 for Chocolate"
                );
                input = scanner.nextInt();
                switch (input){
                    case 1:
                        RequestStatusController.process(new Request(ItemType.COLD_DRINK));
                        break;
                    case 2:
                        RequestStatusController.process(new Request(ItemType.BISCUIT));
                        break;
                    case 3:
                        RequestStatusController.process(new Request(ItemType.CHOCOLATE));
                        break;
                    default:
                        System.exit(1);
                }
            }
        } catch (Exception exception){
            System.out.printf("Error occurred %s\n", exception.getMessage());
        } finally {
            System.out.println("Closing all connections");
            scanner.close();
        }
    }
}


