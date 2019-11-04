// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class So2LiftTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(1, 2, 3, -3, -2, -1, 0, -1, -2, -3, 3, 2);
    Tensor tensor = So2Lift.of(vector);
    tensor.map(Sign::requirePositiveOrZero);
    Chop._12.requireClose(tensor.get(3), RealScalar.of(3.2831853071795862));
    assertEquals(Last.of(tensor), RealScalar.of(2));
  }

  public void testInstance() {
    So2Lift so2Lift = new So2Lift();
    assertEquals(so2Lift.apply(RealScalar.of(-1)), RealScalar.of(-1));
  }
}
