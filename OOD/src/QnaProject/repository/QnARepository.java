package repository;

import model.QuestionAnswers;

import java.util.ArrayList;
import java.util.List;

public class QnARepository {

    private List<QuestionAnswers> questionAnswersList;

    public QnARepository() {
        this.questionAnswersList = new ArrayList<>();
    }

    public void addQnA(QuestionAnswers questionAnswers){
        this.questionAnswersList.add(questionAnswers);
    }

    public List<QuestionAnswers> getQuestionAnswersList() {
        return questionAnswersList;
    }

    public void setQuestionAnswersList(List<QuestionAnswers> questionAnswersList) {
        this.questionAnswersList = questionAnswersList;
    }
}
