// code by jph
package ch.ethz.idsc.owl.ani.rn;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.r2.RotationMatrix;
import junit.framework.TestCase;

public class Ani2dTest extends TestCase {
  public void testSimple() {
    Ani2d ani2d = new Ani2d(RealScalar.of(2), RealScalar.of(1));
    ani2d.setPos(Tensors.vector(1, 2), RotationMatrix.of(1));
    ani2d.setVel(Tensors.vector(-1, 1), RealScalar.of(2));
    ani2d.integrate();
    System.out.println();
  }
}
