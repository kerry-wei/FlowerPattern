import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class ShapesManager {

    private static volatile ShapesManager instance = null;

    ArrayList<GeneralShape> shapes;

    private ShapesManager() {
        shapes = new ArrayList<GeneralShape>();
    }

    public static ShapesManager getInstance() {
        if (instance == null) {
            synchronized (ShapesManager.class) {
                instance = new ShapesManager();
            }
        }
        return instance;
    }

    public GeneralShape tryToSelectShape(MouseEvent event) {
        for (int i = 0; i < shapes.size(); i++) {
            GeneralShape shape = shapes.get(i);
            boolean shapeSelected = shape.hittest(event.getX(), event.getY());
            if (shapeSelected) {

                return shape;
            }
        }

        return null;
    }

    public void addShape(GeneralShape shape) {
        shapes.add(shape);
    }

    public void paintShapes(Graphics2D g2) {
        AffineTransform initialTransform = g2.getTransform();
        for (int i = 0; i < shapes.size(); i++) {
            GeneralShape shape = shapes.get(i);
            shape.paintShape(g2);
            g2.setTransform(initialTransform);
        }
    }

    public void unselectAll() {
        for (int i = 0; i < shapes.size(); i++) {
            shapes.get(i).setSelected(false);
        }
    }

    public void removeShapes() {
        shapes.clear();
    }

    // DEBUG helper
    public void printShapesInfo() {
        for (int i = 0; i < shapes.size(); i++) {
            GeneralShape shape = shapes.get(i);
            System.out.println("shape " + i + ": " + shape.toString());
        }
    }
}
