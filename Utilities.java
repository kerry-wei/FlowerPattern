import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class Utilities {

    static Random rand = new Random();

    static int randomInt(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }

    static float randomFloat() {
        return rand.nextFloat();
    }

    static double translateXToGlobalX(double x, int windowWidth, int windowHeight) {
        double newX = x - windowWidth / 2;
        return newX;
    }

    static double translateYToGlobalY(double y, int windowWidth, int windowHeight) {
        double newY = y - windowHeight / 2;
        return newY;
    }

    static Point2d transformToGlobal(double x, double y) {
        Point2d controlCenter = FlowerPatternPanel.getControlCenter();
        double deltaX = 0.0 - controlCenter.x;
        double deltaY = 0.0 - controlCenter.y;
        AffineTransform T = AffineTransform.getTranslateInstance(deltaX, deltaY);
        return Utilities.transform(T, x, y);
    }

    static Point2d transform(AffineTransform T, Point2d P) {
        Point2D.Double p = new Point2D.Double(P.x, P.y);
        Point2D.Double q = new Point2D.Double();
        T.transform(p, q);
        return new Point2d(q.x, q.y);
    }

    static Point2d transform(AffineTransform T, double x, double y) {
        Point2D.Double p = new Point2D.Double(x, y);
        Point2D.Double q = new Point2D.Double();
        T.transform(p, q);
        return new Point2d(q.x, q.y);
    }

    static Point2d closestPoint(Point2d M, Point2d P0, Point2d P1) {
        Vector2d v = new Vector2d();
        v.sub(P1, P0); // v = P2 - P1

        // early out if line is less than 1 pixel long
        if (v.lengthSquared() < 0.5) {
            return P0;
        }

        Vector2d u = new Vector2d();
        u.sub(M, P0); // u = M - P1

        // scalar of vector projection ...
        double s = u.dot(v) // u dot v 
                / v.dot(v); // v dot v

        // find point for constrained line segment
        if (s < 0) {
            return P0;
        } else if (s > 1) {
            return P1;
        } else {
            Point2d I = P0;
            Vector2d w = new Vector2d();
            w.scale(s, v); // w = s * v
            I.add(w); // I = P1 + w
            return I;
        }
    }

}
