import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class GeneralShape {
    // shape model
    ArrayList<Point2d> points;
    Boolean isFilled = false; // shape is polyline or polygon
    Boolean isClosed = false; // polygon is filled or not
    Color colour = Color.BLACK;
    float strokeThickness = 3.0f;

    double selectThreshold = 5.0;
    double rotateThreshold = 5.0 / 180.0 * Math.PI;

    /**
     * radian angle. If rotateAngle = 0.0, draw no "shadows"
     */
    double rotateAngle = 0.0;

    // for selection
    boolean isSelected;

    // for drawing
    Boolean hasChanged = false; // dirty bit if shape geometry changed
    int[] x_points, y_points;

    public double rotation = 0.0;
    public double scale = 1.0;

    GeneralShape(Color color) {
        this.colour = color;
        this.rotation = 0.0;
        this.scale = 1.0;
    }

    GeneralShape(GeneralShape shape) {
        this.isFilled = shape.isFilled;
        this.isClosed = shape.isClosed;
        this.isSelected = shape.isSelected;
        this.hasChanged = shape.hasChanged;
        this.colour = shape.colour;
        this.selectThreshold = shape.selectThreshold;
        this.strokeThickness = shape.strokeThickness;

        this.x_points = shape.x_points;
        this.y_points = shape.y_points;
        this.points = new ArrayList<Point2d>(shape.points);

    }

    public ArrayList<Point2d> getPoints() {
        return this.points;
    }

    public double getRotateAngle() {
        return this.rotateAngle;
    }

    public void setRotateAngle(double angle) {
        this.rotateAngle = angle;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    // replace all points with array
    public void setPoints(double[][] pts) {
        points = new ArrayList<Point2d>();
        for (double[] p : pts) {
            points.add(new Point2d(p[0], p[1]));
        }
        hasChanged = true;
    }

    // add a point to end of shape
    public void addPoint(double x, double y) {
        if (points == null) {
            points = new ArrayList<Point2d>();
        }

        Point2d transformedPoint = this.transformToGlobal(x, y);
        points.add(transformedPoint);
        hasChanged = true;
    }

    public void paintShape(Graphics2D g2) {
        AffineTransform initialTransform = g2.getTransform();
        this.paint(g2, true);

        g2.setTransform(initialTransform);
        this.paintShadows(g2);
    }

    public void paintShadows(Graphics2D g2) {

        if (rotateAngle > rotateThreshold) {

            //System.out.println("going to paint 'shadows' of angle " + rotateAngle);

            AffineTransform initialTransform = g2.getTransform();
            double rotateAngleAbs = Math.abs(rotateAngle);
            double angleCount = rotateAngleAbs;
            while (angleCount <= Math.PI * 2.0) {
                //System.out.println("angleCount = " + angleCount);

                AffineTransform rotateT = AffineTransform.getRotateInstance(angleCount, 0, 0);

                g2.setTransform(initialTransform);

                // TODO: check it out: why not reverse the order..?
                g2.transform(rotateT);

                this.paint(g2, false);

                angleCount += rotateAngleAbs;
            }
        }
    }

    // paint the shape
    private void paint(Graphics2D g2, boolean highlightOn) {

        //update the shape in java Path2D object if it changed
        if (hasChanged) {
            x_points = new int[points.size()];
            y_points = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                x_points[i] = (int) points.get(i).x;
                y_points[i] = (int) points.get(i).y;
            }
            hasChanged = false;
        }

        //don't draw if path2D is empty (not shape)
        if (x_points != null) {
            if (scale != 1.0) {
                AffineTransform scaleT = AffineTransform.getScaleInstance(scale, scale);
                g2.transform(scaleT);

                //System.out.println("scale = " + scale);
            }

            // special draw for selection
            if (isSelected && highlightOn) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(strokeThickness * 4));
                if (isClosed) {
                    g2.drawPolygon(x_points, y_points, points.size());
                } else {
                    g2.drawPolyline(x_points, y_points, points.size());
                }
            }

            g2.setColor(colour);

            // call right drawing function
            if (isFilled) {
                g2.fillPolygon(x_points, y_points, points.size());
            } else {
                g2.setStroke(new BasicStroke(strokeThickness));
                if (isClosed) {
                    g2.drawPolygon(x_points, y_points, points.size());
                } else {
                    g2.drawPolyline(x_points, y_points, points.size());
                }
            }
        }
    }

    // find closest point
    static Point2d closestPoint(Point2d M, Point2d P1, Point2d P2) {
        return new Point2d();
    }

    // return perpendicular vector
    static public Vector2d perp(Vector2d a) {
        return new Vector2d(-a.y, a.x);
    }

    // line-line intersection
    // return (NaN,NaN) if not intersection, otherwise returns intersecting point
    static Point2d lineLineIntersection(Point2d P0, Point2d P1, Point2d Q0, Point2d Q1) {
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

    Point2d transformToGlobal(double x, double y, double deltaAngle, double deltaScale) {
        Point2d controlCenter = FlowerPatternPanel.getControlCenter();
        double deltaX = 0.0 - controlCenter.x;
        double deltaY = 0.0 - controlCenter.y;

        Point2d ans = new Point2d(0, 0);

        AffineTransform translate = AffineTransform.getTranslateInstance(deltaX, deltaY);
        ans = this.transform(translate, ans.x, ans.y);

        AffineTransform scale = AffineTransform.getScaleInstance(deltaScale, deltaScale);
        ans = this.transform(scale, x, y);

        AffineTransform rotate = AffineTransform.getRotateInstance(deltaAngle, 0, 0);
        ans = this.transform(rotate, ans.x, ans.y);

        return ans;
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
        if (points != null) {

            Point2d transformedPoint = this.transformToGlobal(x, y);

            // find closest two points:
            for (int i = 0; i < points.size() - 1; i++) {
                Point2d p1 = points.get(i);
                Point2d p2 = points.get(i + 1);

                Point2d transformedP1 = this.transformToGlobal(p1.x, p1.y, rotateAngle, scale);
                Point2d transformedP2 = this.transformToGlobal(p2.x, p2.y, rotateAngle, scale);

                if (this.isTouchOnShape(transformedPoint, transformedP1, transformedP2)) {
                    return true;
                } else {
                    // check "shadows":
                    if (rotateAngle >= rotateThreshold) {
                        double angle = Math.abs(rotateAngle);
                        while (angle <= Math.PI * 2) {
                            Point2d shadowP1 = this.transformToGlobal(p1.x, p1.y, angle, scale);
                            Point2d shadowP2 = this.transformToGlobal(p2.x, p2.y, angle, scale);
                            if (this.isTouchOnShape(transformedPoint, shadowP1, shadowP2)) {
                                return true;
                            }
                            
                            angle += Math.abs(rotateAngle);
                        }
                    }
                }

            }

            return false;

        }

        return false;
    }

    private boolean isTouchOnShape(Point2d transformedPoint, Point2d transformedP1, Point2d transformedP2) {
        Point2d closestPoint = Utilities.closestPoint(transformedPoint, transformedP1, transformedP2);
        double distance = transformedPoint.distance(closestPoint);
        if (distance < this.selectThreshold) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String ans = "";
        ans += "color = " + this.colour + "; ";
        ans += "isSelected = " + this.isSelected + "; ";
        ans += "hasChanged = " + this.hasChanged + "; ";
        ans += "number of points" + this.points.size() + "; ";

        return ans;
    }

    public void printShapeInfo() {
        String description = this.toString();

        System.out.println(description);
    }

}
