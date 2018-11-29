package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ApTrajectoryControlTest extends TestCase {
  public void testPseudoDistance() {
    Scalar expected = RealScalar.of(0);
    ApTrajectoryControl trajectoryControl = new ApTrajectoryControl();
    Tensor x = Tensors.vector(0, 0, 0, 1000);
    Tensor y = Tensors.vector(0, 0, 10000, 0);
    Scalar toBeTested = trajectoryControl.pseudoDistance(x, y);
    assertTrue(toBeTested.equals(expected));
  }
}
