// code by jph
package ch.ethz.idsc.sophus.itp.ga;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicInterpolationTest extends TestCase {
  public void testSimple() {
    Tensor sequence = Tensors.fromString("{{1, 2, 3}, {4, 3, 2}, {5, -1, 2.5}}");
    Interpolation interpolation = GeodesicInterpolation.of(Se2CoveringGeodesic.INSTANCE, sequence);
    Chop._12.requireClose( //
        interpolation.at(RealScalar.of(1.2)), //
        Se2CoveringGeodesic.INSTANCE.split(sequence.get(1), sequence.get(2), RealScalar.of(0.2)));
    Chop._12.requireClose( //
        interpolation.at(RealScalar.of(0)), //
        sequence.get(0));
    Chop._12.requireClose( //
        interpolation.at(RealScalar.of(2)), //
        sequence.get(2));
    try {
      interpolation.at(RealScalar.of(-0.01));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      interpolation.at(RealScalar.of(2.01));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
