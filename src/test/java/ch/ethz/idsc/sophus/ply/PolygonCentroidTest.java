// code by jph
package ch.ethz.idsc.sophus.ply;

import java.util.Random;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.sophus.ply.d2.ConvexHull;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PolygonCentroidTest extends TestCase {
  public void testSimple() {
    for (int n = 2; n < 10; ++n) {
      Tensor centroid = PolygonCentroid.of(CirclePoints.of(n));
      Tolerance.CHOP.requireAllZero(centroid);
    }
  }

  public void testSingle() {
    Tensor centroid = PolygonCentroid.of(Tensors.fromString("{{2, 3}}"));
    assertEquals(centroid, Tensors.vector(2, 3));
  }

  public void testRandom() {
    Random random = new Random();
    for (int count = 0; count < 100; ++count) {
      Tensor poly1 = ConvexHull.of(RandomVariate.of(NormalDistribution.standard(), 3 + random.nextInt(3), 2));
      Tensor poly2 = ConvexHull.of(RandomVariate.of(NormalDistribution.standard(), 3 + random.nextInt(3), 2));
      Tensor r12 = PolygonClip.of(poly1).apply(poly2);
      Tensor r21 = PolygonClip.of(poly2).apply(poly1);
      if (r12.length() != r21.length()) {
        System.out.println(poly1);
        System.out.println(poly2);
        fail();
      }
      Chop._10.requireClose(PolygonArea.of(r12), PolygonArea.of(r21));
      if (0 < r12.length())
        Chop._10.requireClose(PolygonCentroid.of(r12), PolygonCentroid.of(r21));
    }
  }

  public void testTranslated() {
    for (int n = 3; n < 10; ++n) {
      Tensor shift = RandomVariate.of(UniformDistribution.unit(), 2);
      Tensor centroid = PolygonCentroid.of(Tensor.of(CirclePoints.of(n).stream().map(shift::add)));
      Tolerance.CHOP.requireClose(centroid, shift);
    }
  }

  public void testSingleFail() {
    AssertFail.of(() -> PolygonCentroid.of(Tensors.fromString("{{2, 3, 4}}")));
  }
}
