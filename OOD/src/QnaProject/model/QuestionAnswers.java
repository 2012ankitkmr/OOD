package model;

import dto.Answer;
import dto.Topic;
import dto.User;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswers {
    private String question;
    private List<Answer> answerList;
    private User user;
    private List<Topic> topics;


    public QuestionAnswers(String question){
        this.question = question;
        this.answerList = new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public void addAnswer(Answer answer){
        this.answerList.add(answer);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString(){
        return "question: "+ question + "\n"+
                "answerList: "+ answerList + "\n"+
                "user: "+ user + "\n"+
                "topics: "+ topics ;
    }

}
