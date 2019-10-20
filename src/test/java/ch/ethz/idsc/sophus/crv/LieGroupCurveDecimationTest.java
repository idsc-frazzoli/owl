// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LieGroupCurveDecimationTest extends TestCase {
  public void testSimple() {
    for (LieGroupCurveDecimation lieGroupCurveDecimation : LieGroupCurveDecimation.values()) {
      CurveDecimation curveDecimation = //
          lieGroupCurveDecimation.of(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, RealScalar.of(0.4));
      Tensor tensor = curveDecimation.apply(RandomVariate.of(UniformDistribution.unit(), 100, 3));
      assertTrue(tensor.length() < 100);
    }
  }

  public void testQuantity() {
    for (LieGroupCurveDecimation lieGroupCurveDecimation : LieGroupCurveDecimation.values()) {
      CurveDecimation curveDecimation = //
          lieGroupCurveDecimation.of(RnGroup.INSTANCE, RnExponential.INSTANCE, Quantity.of(0.7, "m"));
      Tensor tensor = curveDecimation.apply(RandomVariate.of(UniformDistribution.unit(), 100, 3).map(s -> Quantity.of(s, "m")));
      assertTrue(tensor.length() < 90);
    }
  }
}
