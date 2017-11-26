// code by jl
package ch.ethz.idsc.owl.glc.par;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.Se2Parameters;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Power;
import junit.framework.TestCase;

public class ParametersTest extends TestCase {
  public void testDepthToZero() {
    Scalar timeScale = RealScalar.of(10);
    Scalar depthScale = RealScalar.of(5);
    Tensor partitionScale = Tensors.vector(3, 3, 15);
    Scalar dtMax = RationalScalar.of(1, 6);
    int maxIter = 2000;
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Scalar resolution = RationalScalar.of(2, 1); // resolution is bound by Integer.MAX_VALUE
    Scalar oldValue = RealScalar.of(1000);
    Scalar newValue = oldValue;
    long iter = 0;
    while (++iter < 30) {
      resolution = resolution.multiply(RealScalar.of(2));
      Parameters test = new Se2Parameters(//
          resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, stateSpaceModel.getLipschitz());
      oldValue = newValue;
      // from B. Paden: A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning: Formula 4.0.2
      newValue = resolution.divide(test.getDepthLimitExact());
      assertTrue(Scalars.lessEquals(newValue, oldValue));
      if (Scalars.lessThan(newValue.abs(), RealScalar.of(0.001)))
        break;
    }
    // System.out.println(iter);
    assertTrue(iter < 1000);
  }

  public void testPowerofScalar() {
    Scalar scalar = RealScalar.of(0);
    Scalar exponent = RealScalar.of(2);
    Scalar power = Power.of(scalar, exponent);
    assertTrue(Scalars.isZero(power)); // 0 * 0 == 0
  }

  public void testDomainSizeToZero() {
    Scalar timeScale = RealScalar.of(10);
    Scalar depthScale = RealScalar.of(5);
    Tensor partitionScale = Tensors.vector(3, 3, 15);
    Scalar dtMax = RationalScalar.of(1, 6);
    int maxIter = 2000;
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Scalar resolution = RationalScalar.of(2, 1); // resolution is bound by Integer.MAX_VALUE
    Scalar oldValue = RealScalar.of(1000);
    Scalar newValue = oldValue;
    long iter = 0;
    while (++iter < 30) {
      resolution = resolution.multiply(RealScalar.of(2));
      Parameters test = new Se2Parameters(//
          resolution, timeScale, depthScale, partitionScale, dtMax, maxIter, stateSpaceModel.getLipschitz());
      oldValue = newValue;
      newValue = resolution.divide(test.getEta().Get(1));
      // only need to test 1, as only initial conditions different
      assertTrue(Scalars.lessEquals(newValue, oldValue));
      if (Scalars.lessThan(newValue.abs(), RealScalar.of(0.001)))
        break;
    }
    // System.out.println(iter);
    assertTrue(iter < 1000);
  }
}
