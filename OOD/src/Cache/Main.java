package Cache;

import java.util.HashMap;
import java.util.Scanner;

enum EvictionPolicy{
    /***
     * LRU : Leas recently used
     */
    LRU
}
class Node{
    private String key;
    private String data;
    private Node next;
    private Node prev;

    public Node(String key, String data, Node next, Node prev) {
        this.key = key;
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Node(String key, String data) {
        this.key = key;
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
class DLL{
    /***
     * Doubly linked list implementation
     */
    private Node head;
    private Node tail;
    private int size;

    public DLL() {
        head = null;
        tail = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public void addToHead(String key, String data){
        Node temp = new Node(key, data);
        if(head == null){
            head = tail = temp;
        }else{
            temp.setNext(head);
            head.setPrev(temp);
            head = temp;
        }
        size++;
    }

    public void addToHead(Node temp){
        if(head == null){
            head = tail = temp;
        }else{
            temp.setNext(head);
            head.setPrev(temp);
            head = temp;
        }
        size++;
    }

    public void detachNode(Node ref){
        if(ref == null) return;
        Node next = ref.getNext();
        Node prev = ref.getPrev();
        if(next != null) next.setPrev(prev);
        if(prev != null) prev.setNext(next);
        if(head == ref){
            head = next;
        }
        if(tail == ref){
            tail = prev;
        }
        size--;
    }

    public void print(){
        Node curr = this.head;
        System.out.print("(DLL) HEAD: ");
        while (curr != null){
            System.out.printf("(%s, %s ) => ", curr.getKey(), curr.getData());
            curr = curr.getNext();
        }
        System.out.println("END");
    }
}

interface Cache{
    String get(String key);
    void put(String key, String data);
    void print();
}

class LRUCache implements Cache{
    private final HashMap<String, Node> nodeRefMap;
    private DLL dll;
    private Integer maxCacheSize;

    public LRUCache(Integer maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        nodeRefMap = new HashMap<>();
        dll = new DLL();
    }

    public String get(String key){
        Node node = nodeRefMap.getOrDefault(key, null);
        if(node == null) return null;
        dll.detachNode(node);
        dll.addToHead(node);
        return node.getData();
    }

    public void put(String key, String data) {
        if(dll.getSize() == maxCacheSize){
            nodeRefMap.remove(dll.getTail().getKey());
            dll.detachNode(dll.getTail());
        }
        dll.addToHead(key, data);
        nodeRefMap.put(key, dll.getHead());
    }

    public void print(){
        System.out.println("-----------Cache Detail----------");
        dll.print();
        System.out.println("---------------End---------------");
    }
}
class CacheController{
    private static final HashMap<EvictionPolicy, Class<? extends Cache>> cachesClass;
    static {
        cachesClass = new HashMap<EvictionPolicy, Class<? extends Cache>>(){{
           put(EvictionPolicy.LRU, LRUCache.class);
        }};
    }
    public static Cache getCache(EvictionPolicy evictionPolicy, int maxCacheSize) throws Exception {
        Class cacheClass = cachesClass.getOrDefault(EvictionPolicy.LRU, null);
        if(cacheClass == null){
            throw new Exception("Cache class not found for given eviction policy");
        }
        return (Cache) cacheClass.getConstructor(Integer.class).newInstance(maxCacheSize);
    }

}
public class Main {
    public static void main(String[] args) {
        try {
            Cache cache = CacheController.getCache(EvictionPolicy.LRU, 3);
            Scanner scanner = new Scanner(System.in);
            int input;
            while (true){
                System.out.println("Enter 1 for add data\nEnter 2 for fetch data\nEnter 3 for printing cache");
                System.out.print("=> ");
                input = Integer.parseInt(scanner.nextLine());
                switch (input){
                    case 1:
                        {
                            System.out.print("Enter in format (key, data) => ");
                            String line = scanner.nextLine().trim();
                            String key = line.split(",")[0].trim();
                            String data = line.split(",")[1].trim();
                            cache.put(key, data);
                        }
                        break;
                    case 2:
                        {
                            System.out.print("Enter key => ");
                            String key = scanner.nextLine().trim();
                            String data = cache.get(key);
                            System.out.printf("Key %s, Data: %s\n", key, data);
                        }
                        break;
                    case 3:
                        cache.print();
                        break;
                    default:
                        System.out.println("Quiting......");
                        System.exit(1);
                }
                System.out.println();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
