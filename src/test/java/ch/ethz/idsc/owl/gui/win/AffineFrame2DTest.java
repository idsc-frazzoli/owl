// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AffineFrame2DTest extends TestCase {
  public void testSimple() {
    Tensor m1 = Se2Utils.toSE2Matrix(Tensors.vector(1, 2, 3));
    Tensor m2 = Se2Utils.toSE2Matrix(Tensors.vector(-.3, 0.2, .4));
    AffineFrame2D af2 = new AffineFrame2D(m1);
    AffineFrame2D af3 = af2.dot(m2);
    assertEquals(af3.tensor_copy(), m1.dot(m2));
  }

  public void testPoint() {
    Tensor m1 = Se2Utils.toSE2Matrix(Tensors.vector(1, 2, 3));
    AffineFrame2D af2 = new AffineFrame2D(m1);
    Tensor v = Tensors.vector(-.3, -.4, 1);
    Point2D p = af2.toPoint2D(v.Get(0).number().doubleValue(), v.Get(1).number().doubleValue());
    Tensor q = m1.dot(v);
    assertEquals(p.getX(), q.Get(0).number().doubleValue());
    assertEquals(p.getY(), q.Get(1).number().doubleValue());
  }
}
