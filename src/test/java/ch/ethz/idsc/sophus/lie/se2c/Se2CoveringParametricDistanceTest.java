// code by jph / ob 
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringParametricDistanceTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1 + 3, 2 + 4, 3));
    assertEquals(scalar, RealScalar.of(5));
  }

  public void testHalf() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1, 2 + 2 * 3, 3 + Math.PI));
    Chop._14.requireClose(scalar, RealScalar.of(3 * Math.PI));
  }

  public void testSE2() {
    double rand = Math.random();
    Scalar scalarSE2C = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 0), Tensors.vector(1, 2 + 2 * 3, rand * Math.PI));
    Scalar scalarSE2 = Se2ParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 0), Tensors.vector(1, 2 + 2 * 3, rand * Math.PI));
    Chop._14.requireClose(scalarSE2, scalarSE2C);
  }

  public void testSimpleUnits() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.fromString("{1[m], 2[m], 3}"), Tensors.fromString("{4[m], 6[m], 3}"));
    assertEquals(scalar, Quantity.of(5, "m"));
  }

  public void testOtherUnits() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.fromString("{1[m], 2[m], 3}"), Tensors.fromString("{4[m], 6[m], 3.3}"));
    Chop._12.close(scalar, Quantity.of(5.018799335788676, "m"));
  }
}
