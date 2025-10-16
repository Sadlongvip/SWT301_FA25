package traitb.example;

import java.util.logging.Level;
import java.util.logging.Logger;

class SQLInjectionExample {
    private static final Logger logger = Logger.getLogger("SQLInjectionExample");
    public static void main(String[] args) {
        String userInput = "' OR '1'='1";
        String query = "SELECT * FROM users WHERE username = '" + userInput + "'";
        logger.log(Level.SEVERE,() -> "Executing query: " + query);
    }
}
