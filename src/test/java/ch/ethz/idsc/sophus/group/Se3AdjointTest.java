// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3AdjointTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor g = Se3Utils.toMatrix4x4(Rodrigues.exp(RandomVariate.of(distribution, 3)), RandomVariate.of(distribution, 3));
      Se3Adjoint se3Adjoint = new Se3Adjoint(g);
      Tensor u_w = RandomVariate.of(distribution, 2, 3);
      Tensor out = se3Adjoint.apply(u_w);
      assertEquals(Dimensions.of(out), Arrays.asList(2, 3));
      Se3Adjoint se3Inverse = new Se3Adjoint(Inverse.of(g));
      Tensor apply = se3Inverse.apply(out);
      Chop._11.requireClose(u_w, apply);
    }
  }
}
