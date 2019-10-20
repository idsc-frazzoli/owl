// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.LieGroupLineDistance.NormImpl;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class LieGroupLineDistanceTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    LieGroupLineDistance lieGroupLineDistance = //
        Serialization.copy(new LieGroupLineDistance(RnGroup.INSTANCE, RnExponential.INSTANCE::log));
    NormImpl tensorNorm = //
        Serialization.copy(lieGroupLineDistance.tensorNorm(Tensors.vector(1, 2), Tensors.vector(10, 2)));
    assertEquals(tensorNorm.norm(Tensors.vector(5, 2)), RealScalar.ZERO);
    assertEquals(tensorNorm.norm(Tensors.vector(5, 3)), RealScalar.ONE);
  }
}
