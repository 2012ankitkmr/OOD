package repository;

import dto.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private List<User> users;

    public UserRepository() {
        this.users = new ArrayList<>();
    }

    public void addUser(User user){
        this.users.add(user);
    }

    public boolean alreadyRegistered(String name, String designation) {
        return this.users.stream().anyMatch(user -> user.getName().equals(name) && user.getDesignation().equals(designation));
    }
}
