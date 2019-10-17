// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.geom.Point2D;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AffineFrame2DTest extends TestCase {
  public void testSimple() {
    Tensor m1 = Se2Matrix.of(Tensors.vector(1, 2, 3));
    Tensor m2 = Se2Matrix.of(Tensors.vector(-.3, 0.2, .4));
    AffineFrame2D af2 = new AffineFrame2D(m1);
    AffineFrame2D af3 = af2.dot(m2);
    assertEquals(af3.matrix_copy(), m1.dot(m2));
    Point2D point2d = af3.toPoint2D();
    Point2D actual = new Point2D.Double(1.2687737473681602, 1.7596654982619508);
    assertTrue(point2d.distance(actual) < 1e-9);
    assertTrue(point2d.distance(af3.toPoint2D(0, 0)) < 1e-9);
  }

  public void testPoint() {
    Tensor m1 = Se2Matrix.of(Tensors.vector(1, 2, 3));
    AffineFrame2D af2 = new AffineFrame2D(m1);
    Tensor v = Tensors.vector(-.3, -.4, 1);
    Point2D p = af2.toPoint2D(v.Get(0).number().doubleValue(), v.Get(1).number().doubleValue());
    Tensor q = m1.dot(v);
    assertEquals(p.getX(), q.Get(0).number().doubleValue());
    assertEquals(p.getY(), q.Get(1).number().doubleValue());
  }
}
