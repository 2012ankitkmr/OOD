package dto;

public class Answer {
    private String answer;
    private User user;
    private boolean accepted;


    public Answer(String answer, User user) {
        this.answer = answer;
        this.user = user;
        this.accepted =false;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
