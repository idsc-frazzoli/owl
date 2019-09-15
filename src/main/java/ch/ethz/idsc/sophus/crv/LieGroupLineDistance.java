// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class LieGroupLineDistance implements LineDistance, Serializable {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);
  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator log;

  public LieGroupLineDistance(LieGroup lieGroup, TensorUnaryOperator log) {
    this.lieGroup = lieGroup;
    this.log = log;
  }

  @Override // from LineDistance
  public TensorNorm tensorNorm(Tensor beg, Tensor end) {
    LieGroupElement lieGroupElement = lieGroup.element(beg).inverse();
    Tensor normal = NORMALIZE_UNLESS_ZERO.apply(log.apply(lieGroupElement.combine(end)));
    return new TensorNorm() {
      @Override
      public Scalar norm(Tensor index) {
        Tensor vector = log.apply(lieGroupElement.combine(index));
        return Norm._2.ofVector(vector.subtract(vector.dot(normal).pmul(normal)));
      }
    };
  }
}
