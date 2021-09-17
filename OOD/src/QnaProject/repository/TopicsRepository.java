package repository;

import dto.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicsRepository {
    private List<Topic> topics;

    public TopicsRepository() {
        this.topics = new ArrayList<>();
    }

    public void add(Topic topic){
        this.topics.add(topic);
    }


    public Topic getTopicFromName(String name) {
        return this.topics.stream().filter(topic -> topic.getName().equals(name)).findFirst().orElse(null);
    }
}
