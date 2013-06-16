import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class FlowerPatternPanel extends JPanel implements MouseInputListener, ComponentListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    ControlCircle controlCircle;
    ShapesManager shapesManager;

    int canvasWidth;
    int canvasHeight;

    boolean beingDragged;
    boolean isDrawingNewShape;
    JFrame frame;

    /**
     * currentShape is the shape being drawn. currentShape and activeShape can NOT both be non-empty
     */
    GeneralShape currentShape;

    /**
     * activeShape is the shape that is being dragged. currentShape and activeShape can NOT both be non-empty
     */
    GeneralShape activeShape;

    /**
     * shape that is selected.
     */
    GeneralShape selectedShape;

    ArrayList<GeneralShape> shadows;

    /**
     * is global coordinate (relative to (0, 0) point)
     */
    Point2d selectionPoint;

    static FlowerPatternPanel flowerPatternMain;

    static Point2d getControlCenter() {
        double centerX = (double) flowerPatternMain.canvasWidth * 0.5;
        double centerY = (double) flowerPatternMain.canvasHeight * 0.5;
        return new Point2d(centerX, centerY);
    }

    static FlowerPatternPanel getInstance() {
        if (flowerPatternMain == null) {
            flowerPatternMain = new FlowerPatternPanel();
        }

        return flowerPatternMain;
    }

    FlowerPatternPanel() {
        canvasWidth = 600;
        canvasHeight = 600;
        beingDragged = false;
        isDrawingNewShape = false;

        // create control circle:
        controlCircle = new ControlCircle(0, 0);
        controlCircle.setIsClosed(false);
        controlCircle.setIsFilled(false);

        // create ShapesManager:
        shapesManager = ShapesManager.getInstance();

        currentShape = null;
        activeShape = null;
        selectedShape = null;
        shadows = new ArrayList<GeneralShape>();

        selectionPoint = null;

        frame = new JFrame("FlowerPattern");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(canvasWidth, canvasHeight);
        frame.setContentPane(this);
        frame.setVisible(true);

        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);

    }

    private void removeDrawings() {
        currentShape = null;
        activeShape = null;
        selectedShape = null;
        shapesManager.removeShapes();
    }

    // custom graphics drawing 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform initialTransform = g2.getTransform();

        Point2d origin = new Point2d(0, 0);
        Point2d controlCenter = FlowerPatternPanel.getControlCenter();

        double transformX = controlCenter.x - origin.x;
        double transformY = controlCenter.y - origin.y;
        AffineTransform translateT = AffineTransform.getTranslateInstance(transformX, transformY);
        g2.transform(translateT);

        // paint current shape that has NOT been added to shape manager:
        if (currentShape != null) {
            currentShape.paintShape(g2);
        }

        // paint shapes:
        g2.setTransform(initialTransform);
        g2.transform(translateT);
        shapesManager.paintShapes(g2);

        // paint control circle:
        g2.setTransform(initialTransform);
        g2.transform(translateT);
        controlCircle.paint(g2);

        // debug:
        // shapesManager.printShapesInfo();
        // end
    }

    public void updateColor(Color color) {
        controlCircle.setColour(color);
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // System.out.println("mouseClicked");

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {
        // System.out.println("mousePressed: x = " + event.getX() + ", y = " + event.getY());

        if (event.getClickCount() == 2 && controlCircle.hittest(event.getX(), event.getY())) {
            removeDrawings();
        } else {
            if (controlCircle.hittest(event.getX(), event.getY())) {
                Color color = controlCircle.getColour();
                currentShape = new GeneralShape(color);

                currentShape.addPoint(event.getX(), event.getY());
            } else {
                GeneralShape selectedShape = shapesManager.tryToSelectShape(event);
                selectionPoint = Utilities.transformToGlobal(event.getX(), event.getY());

                if (selectedShape != null) {
                    selectedShape.setSelected(!selectedShape.isSelected());
                    if (this.selectedShape != null) {
                        if (!this.selectedShape.equals(selectedShape)) {
                            this.selectedShape.setSelected(false);
                        }

                    }
                    this.selectedShape = selectedShape;
                    this.activeShape = selectedShape;
                } else {
                    shapesManager.unselectAll();
                }
            }
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        // System.out.println("mouseDragged: x = " + event.getX() + ", y = " + event.getY());

        this.beingDragged = true;

        if (currentShape != null) {
            currentShape.addPoint(event.getX(), event.getY());
            currentShape.setSelected(true);
        } else if (activeShape != null) {
            Point2d currentPoint = Utilities.transformToGlobal(event.getX(), event.getY());

            Vector2d initialVector = new Vector2d(selectionPoint.x, selectionPoint.y);
            Vector2d newVector = new Vector2d(currentPoint.x, currentPoint.y);
            double angle = initialVector.angle(newVector);

            /*
            System.out.println("initial vector = " + initialVector.toString());
            System.out.println("new vector = " + newVector.toString());
            System.out.println("angle between initial vector and new vector = " + angle);
             */

            double scale = newVector.length() / initialVector.length();
            activeShape.setScale(scale);

            System.out.println("new scale = " + scale);

            // update "shadows" accordingly
            double angleDegree = angle / Math.PI * 180.0;
            if (angleDegree >= 5.0) {
                // System.out.println("valid angle degree detected: " + angleDegree);

                activeShape.setRotateAngle(angle);
            }
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        // System.out.println("mouseReleased: x = " + event.getX() + ", y = " + event.getY());

        if (!beingDragged && controlCircle.hittest(event.getX(), event.getY())) {
            Color randomColor = Color.getHSBColor((float) Utilities.randomFloat() / 1.f, (float) Utilities.randomFloat() / 1.f, (float) Utilities.randomFloat() / 1.f);
            controlCircle.setColour(randomColor);

        } else if (beingDragged && activeShape != null) {
            activeShape.setSelected(false);
            activeShape = null;
        }

        // add point to currentShape, if it's not null
        if (currentShape != null) {
            currentShape.addPoint(event.getX(), event.getY());
            currentShape.setSelected(false);
            // add shape to ShapesManager if user was dragging and dragging originated from control circle
            if (beingDragged) {
                shapesManager.addShape(currentShape);

            }

        }

        currentShape = null;
        activeShape = null;
        selectionPoint = null;

        this.beingDragged = false;

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        // System.out.println("mouseMoved");
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {

    }

    @Override
    public void componentMoved(ComponentEvent arg0) {

    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        repaint();

        int width = frame.getWidth();
        int height = frame.getHeight();
        System.out.println("new width = " + width + ", new height = " + height);

        canvasWidth = width;
        canvasHeight = height;
    }

    @Override
    public void componentShown(ComponentEvent arg0) {

    }

}
