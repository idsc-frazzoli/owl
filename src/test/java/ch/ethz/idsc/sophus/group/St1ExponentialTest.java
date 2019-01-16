package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class St1ExponentialTest extends TestCase {
    public void testExpLog() {
      Scalar u = RealScalar.of(7);
      Scalar v = RealScalar.of(3);
      Tensor inp = Tensors.of(u,v);
  
      Tensor xy = St1Exponential.INSTANCE.exp(inp);
      Tensor uv = St1Exponential.INSTANCE.log(xy);
      Chop._12.requireClose(inp, uv);
    }

    public void testLogExp() {
      Scalar u = RealScalar.of(7);
      Scalar v = RealScalar.of(3);
      Tensor inp = Tensors.of(u,v);
  

      Tensor uv = St1Exponential.INSTANCE.log(inp);
      Tensor xy = St1Exponential.INSTANCE.exp(uv);
      Chop._12.requireClose(inp, xy);
    }
}
