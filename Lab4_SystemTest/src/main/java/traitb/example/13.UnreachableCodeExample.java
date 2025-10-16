package traitb.example;


import java.util.logging.Logger;

class UnreachableCodeExample {
    private static final Logger logger = Logger.getLogger(UnreachableCodeExample.class.getName());
    private static int number = 42;

    public static void main(String[] args) {
        String str = String.valueOf(number);
        logger.info(str);
    }
}
