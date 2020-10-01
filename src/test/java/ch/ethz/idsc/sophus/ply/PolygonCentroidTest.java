// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class PolygonCentroidTest extends TestCase {
  public void testSimple() {
    for (int n = 3; n < 10; ++n) {
      Tensor centroid = PolygonCentroid.FUNCTION.apply(CirclePoints.of(n));
      Tolerance.CHOP.requireAllZero(centroid);
    }
  }

  public void testTranslated() {
    for (int n = 3; n < 10; ++n) {
      Tensor shift = RandomVariate.of(UniformDistribution.unit(), 2);
      Tensor centroid = PolygonCentroid.FUNCTION.apply(Tensor.of(CirclePoints.of(n).stream().map(shift::add)));
      Tolerance.CHOP.requireClose(centroid, shift);
    }
  }
}
