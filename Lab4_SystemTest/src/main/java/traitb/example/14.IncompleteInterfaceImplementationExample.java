package traitb.example;

import java.util.logging.Logger;

interface Shape {
    void draw();
    void resize();
}

class Square implements Shape {
    Logger logger = Logger.getLogger(Square.class.getName());
    @Override
    public void draw() {
        logger.info("Drawing square");
    }

    @Override
    public void resize() {
        // add comment to explain why this function can't do anything
    }

}
