// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class SymmetricLineDistance implements LineDistance, Serializable {
  private final LineDistance lineDistance;

  public SymmetricLineDistance(LineDistance lineDistance) {
    this.lineDistance = lineDistance;
  }

  @Override // from LineDistance
  public TensorNorm tensorNorm(Tensor beg, Tensor end) {
    TensorNorm tensorNorm1 = lineDistance.tensorNorm(beg, end);
    TensorNorm tensorNorm2 = lineDistance.tensorNorm(end, beg);
    return new TensorNorm() {
      @Override
      public Scalar norm(Tensor index) {
        return Max.of( //
            tensorNorm1.norm(index), //
            tensorNorm2.norm(index));
      }
    };
  }
}
