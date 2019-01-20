// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StExponentialTest extends TestCase {
  public void testSt1ExpLog() {
    Scalar u = RealScalar.of(7);
    Scalar v = RealScalar.of(3);
    Tensor inp = Tensors.of(u, v);
    Tensor xy = StExponential.INSTANCE.exp(inp);
    Tensor uv = StExponential.INSTANCE.log(xy);
    Chop._12.requireClose(inp, uv);
  }

  public void testSt1LogExp() {
    Scalar u = RealScalar.of(7);
    Scalar v = RealScalar.of(3);
    Tensor inp = Tensors.of(u, v);
    Tensor uv = StExponential.INSTANCE.log(inp);
    Tensor xy = StExponential.INSTANCE.exp(uv);
    Chop._12.requireClose(inp, xy);
  }

  public void testSt1ExpLogRandom() {
    for (int count = 0; count < 100; ++count) {
      Distribution distribution = NormalDistribution.standard();
      Tensor inp = RandomVariate.of(distribution, 2);
      Tensor xy = StExponential.INSTANCE.exp(inp);
      Tensor uv = StExponential.INSTANCE.log(xy);
      Chop._12.requireClose(inp, uv);
    }
  }

  public void testSt1Singular() {
    Tensor inp = Tensors.vector(0, 2);
    Tensor xy = StExponential.INSTANCE.exp(inp);
    Tensor uv = StExponential.INSTANCE.log(xy);
    Chop._12.requireClose(inp, uv);
  }

  public void testExpLog() {
    Scalar u = RealScalar.of(Math.random());
    Tensor v = Tensors.vector(Math.random(), 3 * Math.random(), -Math.random(), -4 * Math.random());
    Tensor inp = Tensors.of(u, v);
    Tensor xy = StExponential.INSTANCE.exp(inp);
    Tensor uv = StExponential.INSTANCE.log(xy);
    Chop._12.requireClose(inp, uv);
  }

  public void testLogExp() {
    Scalar u = RealScalar.of(Math.random());
    Tensor v = Tensors.vector(Math.random(), 3 * Math.random(), -Math.random(), -4 * Math.random());
    Tensor inp = Tensors.of(u, v);
    Tensor uv = StExponential.INSTANCE.log(inp);
    Tensor xy = StExponential.INSTANCE.exp(uv);
    Chop._12.requireClose(inp, xy);
  }

  public void testSingular() {
    Tensor v = Tensors.vector(Math.random(), 3 * Math.random(), -Math.random(), -4 * Math.random());
    Tensor inp = Tensors.of(RealScalar.ZERO, v);
    Tensor xy = StExponential.INSTANCE.exp(inp);
    Tensor uv = StExponential.INSTANCE.log(xy);
    Chop._12.requireClose(inp, uv);
  }
}
