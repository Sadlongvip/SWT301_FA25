package traitb.example;

import java.util.logging.Logger;

class CatchGenericExceptionExample {
    public static void main(String[] args) {
        final Logger logger = Logger.getLogger(CatchGenericExceptionExample.class.getName());
        try {
            String s = null;
            logger.info(s);
        } catch (Exception _) {
            logger.info("Something went wrong");
        }
    }
}
