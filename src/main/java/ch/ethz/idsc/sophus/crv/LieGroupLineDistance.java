// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

public class LieGroupLineDistance implements LineDistance, Serializable {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);
  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator log;

  public LieGroupLineDistance(LieGroup lieGroup, TensorUnaryOperator log) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.log = Objects.requireNonNull(log);
  }

  @Override // from LineDistance
  public NormImpl tensorNorm(Tensor beg, Tensor end) {
    LieGroupElement lieGroupElement = lieGroup.element(beg).inverse();
    return new NormImpl( //
        lieGroupElement, //
        NORMALIZE_UNLESS_ZERO.apply(log.apply(lieGroupElement.combine(end))));
  }

  public class NormImpl implements TensorNorm {
    private final LieGroupElement lieGroupElement;
    private final Tensor normal;

    public NormImpl(LieGroupElement lieGroupElement, Tensor normal) {
      this.lieGroupElement = lieGroupElement;
      this.normal = normal;
    }

    /** @param tensor of the lie group
     * @return element of the lie algebra */
    public Tensor project(Tensor tensor) {
      Tensor vector = log.apply(lieGroupElement.combine(tensor));
      return vector.dot(normal).pmul(normal);
    }

    /** @param tensor of the lie group
     * @return element of the lie algebra */
    public Tensor orthogonal(Tensor tensor) {
      Tensor vector = log.apply(lieGroupElement.combine(tensor)); // redundant to project
      return vector.subtract(vector.dot(normal).pmul(normal)); // ... but vector has to be stored
    }

    @Override // from TensorNorm
    public Scalar norm(Tensor tensor) {
      return Norm._2.ofVector(orthogonal(tensor));
    }
  }
}
