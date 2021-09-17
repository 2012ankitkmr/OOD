package service;

import dto.Answer;
import dto.Topic;
import dto.User;
import model.QuestionAnswers;
import repository.QnARepository;
import repository.TopicsRepository;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserManagementService {

    private UserRepository userData;
    private User loggedInUser;
    private TopicsRepository topicsRepository;
    private QnARepository qnARepository;

    public UserManagementService(UserRepository userRepository, TopicsRepository topicsRepository, QnARepository qnARepository){
        this.topicsRepository = topicsRepository;
        this.qnARepository = qnARepository;
        this.userData = userRepository;
    }

    public User signUp(String name, String designation) throws Exception {
        User user = null;
        if(!userData.alreadyRegistered(name, designation)) {
            user = new User(name, designation);
            userData.addUser(user);
            logIn(user);
        }
        else throw new Exception("Already Registered!");
        return user;
    }

    public void logIn(User user) throws Exception {
        if(this.loggedInUser !=null) {
            throw new Exception("User Already Logged in : " + user.getName());
        }
        this.loggedInUser = user;
    }

    public void logOut() {
        this.loggedInUser = null;
    }

    public void subscribe(List<String> topicList) throws Exception {
        validateUser();
        List<Topic> topics= new ArrayList<>();
        for(String topic: topicList){
            Topic t = topicsRepository.getTopicFromName(topic);
            if(null == t){
                Topic tempTopic = new Topic(topic);
                topics.add(tempTopic);
                topicsRepository.add(tempTopic);
            }
            else{
                topics.add(t);
            }
        }
        this.loggedInUser.getSubscribedTopics().addAll(topics);
    }

    private void validateUser() throws Exception {
        if(this.loggedInUser == null)
            throw new Exception("No User Logged in");
    }

    public User showProfile() throws Exception {
        validateUser();
        return this.loggedInUser;
    }

    public void addQuestions(String question, List<String> qnATopics) throws Exception {
        validateUser();
        QuestionAnswers questionAnswers = new QuestionAnswers(question);
        List<Topic> topics= new ArrayList<>();
        for(String topic: qnATopics){
            Topic t = topicsRepository.getTopicFromName(topic);
            if(null == t){
                Topic tempTopic = new Topic(topic);
                topics.add(tempTopic);
                topicsRepository.add(tempTopic);
            }
            else{
                topics.add(t);
            }
        }


        questionAnswers.setTopics(topics);
        questionAnswers.setUser(this.loggedInUser);
        this.qnARepository.addQnA(questionAnswers);
        this.loggedInUser.addQnA(questionAnswers);
    }

    public void answerQuestion(QuestionAnswers questionAnswers, Answer answer) throws Exception{
        validateUser();
        questionAnswers.addAnswer(answer);
    }

    public void acceptAnswer(QuestionAnswers questionAnswers, Answer answer) throws Exception {
        validateUser();
        answer.getUser().incrementRating();
        answer.setAccepted(true);
    }

    public List<QuestionAnswers> showFeed(){
        return this.qnARepository.getQuestionAnswersList();
    }


    public List<QuestionAnswers> showFeed(List<String> topicsStrList){
        return this.qnARepository.getQuestionAnswersList().stream()
                .filter(qna -> qna.getTopics().stream().map(Topic::getName).collect(Collectors.toList()).stream().anyMatch(topicsStrList::contains)
                ).collect(Collectors.toList());
    }

    public List<QuestionAnswers> showFeed(boolean answered){
        return this.qnARepository.getQuestionAnswersList().stream().filter( qnA -> !qnA.getAnswerList().isEmpty()).collect(Collectors.toList());
    }


}
