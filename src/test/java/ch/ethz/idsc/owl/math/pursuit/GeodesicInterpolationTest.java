// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicInterpolationTest extends TestCase {
  public void testSimple() {
    Tensor sequence = Tensors.fromString("{{1,2,3},{4,3,2},{5,-1,2.5}}");
    GeodesicInterpolation geodesicInterpolation = //
        new GeodesicInterpolation(Se2CoveringGeodesic.INSTANCE, sequence);
    Chop._12.requireClose( //
        geodesicInterpolation.at(RealScalar.of(1.2)), //
        Se2CoveringGeodesic.INSTANCE.split(sequence.get(1), sequence.get(2), RealScalar.of(0.2)));
    Chop._12.requireClose( //
        geodesicInterpolation.at(RealScalar.of(0)), //
        sequence.get(0));
    Chop._12.requireClose( //
        geodesicInterpolation.at(RealScalar.of(2)), //
        sequence.get(2));
  }
}
