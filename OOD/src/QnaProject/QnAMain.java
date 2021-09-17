import dto.Topic;
import repository.QnARepository;
import repository.TopicsRepository;
import repository.UserRepository;
import service.UserManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QnAMain {

// initilaize all topics

    public static void main(String[] args) throws Exception {
        UserRepository userRepository = new UserRepository();
        TopicsRepository topicsRepository = new TopicsRepository();
        QnARepository qnARepository = new QnARepository();
        UserManagementService userManagementService = new UserManagementService(userRepository, topicsRepository, qnARepository);
        
        System.out.println(userManagementService.signUp("Sachin", "Developer"));
        userManagementService.subscribe(Arrays.asList("java", "hadoop", "jdk"));
        userManagementService.addQuestions("What are new open source jdks?", Arrays.asList("java", "jdk"));
        userManagementService.addQuestions("Does Hadoop work on JDK 11?", Arrays.asList("hadoop", "jdk"));
        System.out.println(userManagementService.showFeed());
        System.out.println(userManagementService.showFeed(Arrays.asList("java")));
        System.out.println(userManagementService.showFeed(Arrays.asList("jdk")));
        System.out.println(userManagementService.showFeed(true));
        userManagementService.logOut();

//        System.out.println(userManagementService.signUp("Kalyan", "Developer"));
//        userManagementService.subscribe(Arrays.asList("java", "hadoop", "jdk"));
//        userManagementService.addQuestions("What are new open source jdks?", Arrays.asList("java", "jdk"));
//        userManagementService.addQuestions("Does Hadoop work on JDK 11?", Arrays.asList("hadoop", "jdk"));
//        System.out.println(userManagementService.showFeed());
//        System.out.println(userManagementService.showFeed(Arrays.asList("java")));
//        System.out.println(userManagementService.showFeed(Arrays.asList("jdk")));
//        System.out.println(userManagementService.showFeed(true));
//        userManagementService.logOut();


    }

}
