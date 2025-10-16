package traitb.example;

import java.util.logging.Logger;

class NullPointerExample {
    private static final Logger logger = Logger.getLogger(NullPointerExample.class.getName());
    public static void main(String[] args) {
        String text = null;
        if (text != null) {
            logger.info("Text is not empty");
        } else  {
            logger.info("Text is empty");
        }

    }
}
