import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class ControlCircle {

    Boolean isFilled = false; // shape is polyline or polygon
    Boolean isClosed = false; // polygon is filled or not
    Color colour = Color.BLACK;
    float strokeThickness = 3.0f;

    /**
     * top-left x
     */
    int x;

    /**
     * top-left y
     */
    int y;

    int width;
    int height;

    /**
     * center of the circle
     */
    int centerX;
    int centerY;

    int radius;

    ControlCircle(int x, int y) {
        //Point2d transformedPoint = this.transformToGlobal(x, y);

        this.centerX = x;
        this.centerY = y;

        this.width = 20;
        this.height = this.width;
        this.radius = this.width / 2;
        this.x = this.centerX - this.radius;
        this.y = this.centerY - this.radius;

    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public void setStrokeThickness(float strokeThickness) {
        this.strokeThickness = strokeThickness;
    }

    public Boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(Boolean isFilled) {
        this.isFilled = isFilled;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    // for selection
    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // for drawing
    Boolean hasChanged = false; // dirty bit if shape geometry changed
    int[] x_points, y_points;

    public float rotation = 0;
    public double scale = 1.0;

    // paint the control circle
    public void paint(Graphics2D g2) {
        g2.setColor(this.colour);

        /*
        Point2d origin = new Point2d(0, 0);
        Point2d controlCenter = FlowerPatternMain.getControlCenter();

        double transformX = controlCenter.x - origin.x;
        double transformY = controlCenter.y - origin.y;
        AffineTransform T = AffineTransform.getTranslateInstance(transformX, transformY);
        g2.setTransform(T);
        */

        g2.fillOval(x, y, width, height);
    }

    // find closest point
    static Point2d closestPoint(Point2d M, Point2d P1, Point2d P2) {
        // TODO: implement

        return new Point2d();
    }

    // return perpendicular vector
    static public Vector2d perp(Vector2d a) {
        return new Vector2d(-a.y, a.x);
    }

    // line-line intersection
    // return (NaN,NaN) if not intersection, otherwise returns intersecting point
    static Point2d lineLineIntersection(Point2d P0, Point2d P1, Point2d Q0, Point2d Q1) {

        // TODO: implement

        return new Point2d();
    }

    // affine transform helper
    // return P_prime = T * P   
    Point2d transformToGlobal(double x, double y) {
        Point2d controlCenter = FlowerPatternPanel.getControlCenter();
        double deltaX = 0.0 - controlCenter.x;
        double deltaY = 0.0 - controlCenter.y;
        AffineTransform T = AffineTransform.getTranslateInstance(deltaX, deltaY);
        return this.transform(T, x, y);
    }

    Point2d transform(AffineTransform T, Point2d P) {
        Point2D.Double p = new Point2D.Double(P.x, P.y);
        Point2D.Double q = new Point2D.Double();
        T.transform(p, q);
        return new Point2d(q.x, q.y);
    }

    Point2d transform(AffineTransform T, double x, double y) {
        Point2D.Double p = new Point2D.Double(x, y);
        Point2D.Double q = new Point2D.Double();
        T.transform(p, q);
        return new Point2d(q.x, q.y);
    }

    // hit test with this shape
    public boolean hittest(double x, double y) {
        //Point2d transformedPoint = this.transformPoint(x, y);

        /*
        Point2d localPoint = FlowerPatternMain.getControlCenter();
        double deltaX = localPoint.x - this.x;
        double deltaY = localPoint.y - this.y;
        AffineTransform T = AffineTransform.getTranslateInstance(deltaX, deltaY);
        */
        Point2d transformedPoint = this.transformToGlobal(x, y);

        float distanceToCenter = (float) Math.sqrt(Math.pow(transformedPoint.x - this.centerX, 2)
                + Math.pow(transformedPoint.y - this.centerY, 2));
        if (distanceToCenter <= this.radius) {
            return true;
        } else {
            return false;
        }
    }
}
