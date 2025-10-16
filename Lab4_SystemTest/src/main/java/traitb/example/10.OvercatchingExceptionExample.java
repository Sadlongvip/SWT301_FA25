package traitb.example;

import java.util.logging.Logger;

class OvercatchingExceptionExample {
    private static final Logger logger = Logger.getLogger(OvercatchingExceptionExample.class.getName());
    public static void main(String[] args) {
        try {
            int[] arr = new int[5];
            String str = String.valueOf(arr[10]);
            logger.info(str);
        } catch (RuntimeException _) {
            logger.info("Runtime error");
        }
    }
}
