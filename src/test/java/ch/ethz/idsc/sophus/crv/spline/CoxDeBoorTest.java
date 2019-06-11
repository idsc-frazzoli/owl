// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.util.Arrays;
import java.util.HashSet;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CoxDeBoorTest extends TestCase {
  public void testSimple() {
    Tensor knots = Tensors.vector(0, 1, 2, 3, 4, 5, 6);
    Tensor control = Tensors.vector(-1, 2, 0, -1);
    Scalar x = RealScalar.of(2.5);
    {
      CoxDeBoor coxDeBoor = new CoxDeBoor(2, knots, control);
      Tensor v2 = coxDeBoor.apply(x);
      assertEquals(v2, RealScalar.of(1.375));
      assertEquals(coxDeBoor.set, new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6)));
    }
    {
      // DeBoor deBoor = DeBoor.of(knots, control);
      // System.out.println(deBoor.degree());
      // System.out.println(deBoor.apply(x));
    }
  }
}
