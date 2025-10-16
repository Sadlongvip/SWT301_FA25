package traitb.example;

import java.util.logging.Level;
import java.util.logging.Logger;

class User {
    Logger logger = Logger.getLogger(User.class.getName());
    private String name;
    private int age;

    public void display() {
        logger.log(Level.SEVERE, () -> "Name: " + name + ", Age: " + age);
    }
}
