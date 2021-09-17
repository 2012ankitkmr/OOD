package dto;

import model.QuestionAnswers;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private String designation;
    private List<Topic> subscribedTopics;
    private Integer ratings;
    private List<QuestionAnswers> questionAnswers;

    public User(String name, String designation) {
        this.name = name;
        this.designation = designation;
        this.subscribedTopics = new ArrayList<>();
        this.ratings = 0;
        this.questionAnswers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<Topic> getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(List<Topic> subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }

    public void incrementRating(){
        this.ratings++;
    }

    public Integer getRatings() {
        return ratings;
    }

    public void setRatings(Integer ratings) {
        this.ratings = ratings;
    }

    public void addQnA(QuestionAnswers questionAnswers){
        this.questionAnswers.add(questionAnswers);
    }
}
