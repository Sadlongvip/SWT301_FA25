package traitb.example;

import java.util.logging.Logger;

class Printer {
    Logger logger = Logger.getLogger(Printer.class.getName());
    void print(String message) {
        logger.info(message);
    }
}

public class Report {
    private final Printer printer = new Printer(); // tightly coupled

    void generate() {
        printer.print("Generating report...");
    }
}
