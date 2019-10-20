// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

public class LieProjectedLineDistance implements LineDistance, Serializable {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);
  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  public LieProjectedLineDistance(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  @Override // from LineDistance
  public TensorNorm tensorNorm(Tensor beg, Tensor end) {
    LieGroupElement lieBeg = lieGroup.element(beg);
    LieGroupElement lieInv = lieBeg.inverse();
    Tensor normal = NORMALIZE_UNLESS_ZERO.apply(lieExponential.log(lieInv.combine(end)));
    // TODO design so that serializable
    return new TensorNorm() {
      @Override // from TensorNorm
      public Scalar norm(Tensor tensor) {
        Tensor vector = lieExponential.log(lieInv.combine(tensor));
        Tensor project = vector.dot(normal).pmul(normal);
        Tensor along = lieBeg.combine(lieExponential.exp(project));
        Tensor dir = lieGroup.element(along).inverse().combine(tensor);
        return Norm._2.ofVector(lieExponential.log(dir));
      }
    };
  }
}
