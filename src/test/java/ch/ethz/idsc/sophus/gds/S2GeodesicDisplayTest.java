// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Arrays;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class S2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    Tensor tensor = S2Display.tangentSpace(Tensors.vector(0, 1, 0));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 3));
  }

  public void testInvariant() {
    ManifoldDisplay geodesicDisplay = S2Display.INSTANCE;
    Tensor xyz = geodesicDisplay.project(Tensors.vector(1, 2, 0));
    Tensor xy = geodesicDisplay.toPoint(xyz);
    Tolerance.CHOP.requireClose(Vector2Norm.of(xy), RealScalar.ONE);
  }

  public void testTangent() {
    Tensor xyz = Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 0.3, 0.5));
    Tensor matrix = S2Display.tangentSpace(xyz);
    assertEquals(Dimensions.of(matrix), Arrays.asList(2, 3));
    Tolerance.CHOP.requireAllZero(matrix.dot(xyz));
  }

  public void testProjTangent() {
    S2Display s2GeodesicDisplay = (S2Display) S2Display.INSTANCE;
    for (int index = 0; index < 10; ++index) {
      Tensor xya = RandomVariate.of(NormalDistribution.standard(), 3);
      Tensor xyz = s2GeodesicDisplay.project(xya);
      Tensor tan = s2GeodesicDisplay.createTangent(xya);
      Tolerance.CHOP.requireAllZero(xyz.dot(tan));
    }
  }

  public void testFail() {
    AssertFail.of(() -> S2Display.tangentSpace(Tensors.vector(1, 1, 1)));
  }
}
