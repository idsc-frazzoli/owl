// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class HeExponentialTest extends TestCase {
  public void testExpLog() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor u = RandomVariate.of(distribution, 3);
      Tensor v = RandomVariate.of(distribution, 3);
      Tensor w = RandomVariate.of(distribution);
      Tensor inp = Tensors.of(u, v, w);
      Tensor xyz = HeExponential.INSTANCE.exp(inp);
      Tensor uvw = HeExponential.INSTANCE.log(xyz);
      Chop._12.requireClose(inp, uvw);
    }
  }

  public void testLogExp() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor x = RandomVariate.of(distribution, 3);
      Tensor y = RandomVariate.of(distribution, 3);
      Tensor z = RandomVariate.of(distribution);
      Tensor inp = Tensors.of(x, y, z);
      Tensor uvw = HeExponential.INSTANCE.log(inp);
      Tensor xyz = HeExponential.INSTANCE.exp(uvw);
      Chop._12.requireClose(inp, xyz);
    }
  }
}
