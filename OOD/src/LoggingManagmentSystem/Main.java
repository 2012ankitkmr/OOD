package LoggingManagmentSystem;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main{
    static SearchService searchService;
    static LoggerQueue loggerQueue;
    static LoggerClient loggerClient;
    static LoggerServer loggerServer;

    static {
        searchService = new SearchService();
        loggerQueue = new LoggerQueue();
        loggerClient = new LoggerClient(loggerQueue);
        loggerServer = new LoggerServer(loggerQueue, searchService);
    }

    static void addShutDownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(loggerServer.isAlive()){
                loggerServer.stop();
            }
        }));
    }

    public static void main(String[] args) {
        addShutDownHook();
        try(Scanner scanner = new Scanner(System.in)){
            int input;
            Integer logOrdinal;
            String logMessage;
            List<String> queryList;
            loggerServer.start();
            boolean run = true;
            while (run){
                System.out.printf("%s\n%s\n%s\n",
                        "Enter 1 for adding log",
                        "Enter 2 for query index",
                        "Enter 3 for printing all indexes"
                );
                System.out.print(":=> ");
                input = Integer.parseInt(scanner.nextLine());
                switch (input){
                    case 1:
                        System.out.print("Enter the log level (1 for INFO, 2 for DEBUG, 3 for ERROR) => ");
                        logOrdinal = Integer.parseInt(scanner.nextLine());
                        while (LogLevel.getByOrdinal(logOrdinal) == null){
                            System.out.print("Please enter the valid log level => ");
                            logOrdinal = Integer.parseInt(scanner.nextLine());
                        }
                        System.out.print("Enter the log message => ");
                        logMessage = scanner.nextLine();
                        while (logMessage.length() == 0){
                            System.out.print("Please enter again => ");
                            logMessage = scanner.nextLine();
                        }
                        loggerClient.pushLog(LogLevel.getByOrdinal(logOrdinal), logMessage);
                        System.out.println("Log created successfully for message!!");
                        break;

                    case 2:
                        System.out.print("Enter queries => ");
                        queryList = Arrays.stream(scanner.nextLine().split(" ")).
                                map(String::trim).filter(d -> d.length() > 0).collect(Collectors.toList());
                        searchService.outputQueryResults(queryList);
                        break;

                    case 3:
                        searchService.outputAllIndexes();
                        break;

                    default:
                        run = false;
                        break;
                }
                System.out.println();
            }
        } catch (Exception exception){
            System.out.printf("Error occurred %s\n", exception.getMessage());
        } finally {
            if(loggerServer.isAlive()){
                loggerServer.stop();
            }
        }
    }
}
