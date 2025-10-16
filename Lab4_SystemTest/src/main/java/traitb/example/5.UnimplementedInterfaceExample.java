package traitb.example;

interface Drawable {
    void draw();
}

class Circle implements Drawable {
    @Override
    public void draw() {
        // Missing draw() implementation → compile error
    }

}
