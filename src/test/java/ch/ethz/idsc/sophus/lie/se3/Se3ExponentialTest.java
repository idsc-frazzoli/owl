// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3ExponentialTest extends TestCase {
  public void testSimple() {
    Tensor input = Tensors.of( //
        Tensors.vector(1, 2, 3), //
        Tensors.vector(.2, .3, -.1));
    Tensor g = Se3Exponential.INSTANCE.exp(input);
    Tensor u_w = Se3Exponential.INSTANCE.log(g);
    Chop._12.requireClose(input, u_w);
  }

  public void testUnits() {
    Tensor input = Tensors.of( //
        Tensors.fromString("{1[m*s^-1], 2[m*s^-1], 3[m*s^-1]}"), //
        Tensors.vector(.2, .3, -.1));
    Tensor g = Se3Exponential.INSTANCE.exp(input);
    Tensor u_w = Se3Exponential.INSTANCE.log(g);
    Chop._12.requireClose(input, u_w);
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.of(0, .2);
    for (int index = 0; index < 100; ++index) {
      Tensor input = Tensors.of( //
          RandomVariate.of(distribution, 3), //
          RandomVariate.of(distribution, 3));
      Tensor g = Se3Exponential.INSTANCE.exp(input);
      Tensor u_w = Se3Exponential.INSTANCE.log(g);
      Chop._12.requireClose(input, u_w);
    }
  }

  public void testZero() {
    Tensor input = Tensors.of( //
        Tensors.vector(1, 2, 3), //
        Tensors.vector(0, 0, 0));
    Tensor g = Se3Exponential.INSTANCE.exp(input);
    assertEquals(g, Se3Matrix.of(IdentityMatrix.of(3), input.get(0)));
    Tensor u_w = Se3Exponential.INSTANCE.log(g);
    Chop._12.requireClose(input, u_w);
  }

  public void testAlmostZero() {
    Tensor input = Tensors.of( //
        Tensors.vector(1, 2, 3), //
        Tensors.vector(1e-15, 1e-15, -1e-15));
    Tensor g = Se3Exponential.INSTANCE.exp(input);
    Tensor u_w = Se3Exponential.INSTANCE.log(g);
    Chop._12.requireClose(input, u_w);
  }
}
