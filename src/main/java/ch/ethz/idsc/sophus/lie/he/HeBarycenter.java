// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class HeBarycenter implements TensorUnaryOperator {
  // private final Tensor sequence;
  public HeBarycenter(Tensor sequence) {
    // this.sequence = sequence;
  }

  @Override
  public Tensor apply(Tensor mean) {
    // Tensor xMean = mean.get(0);
    // Tensor yMean = mean.get(1);
    // FIXME JPH
    // Scalar z = mean.Get(2).add(xMean.dot(yMean).subtract(xyMean).multiply(RationalScalar.HALF));
    return null;
  }
}
