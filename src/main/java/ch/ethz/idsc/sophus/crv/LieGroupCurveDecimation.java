// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** @see CurveDecimation */
/* package */ class LieGroupCurveDecimation implements CurveDecimation {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);
  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator tangent;
  private final Scalar epsilon;

  public LieGroupCurveDecimation(LieGroup lieGroup, TensorUnaryOperator tangent, Scalar epsilon) {
    this.lieGroup = lieGroup;
    this.tangent = tangent;
    this.epsilon = epsilon;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return evaluate(tensor).result();
  }

  @Override
  public Result evaluate(Tensor tensor) {
    return Tensors.isEmpty(tensor) //
        ? EmptyResult.INSTANCE
        : new LieGroupResult(tensor);
  }

  private class LieGroupResult implements Result, Serializable {
    private final Tensor[] tensors;
    private final Scalar[] scalars;
    private final List<Integer> list = new LinkedList<>();

    /** @param tensor of length at least 1 */
    public LieGroupResult(Tensor tensor) {
      tensors = tensor.stream().toArray(Tensor[]::new);
      scalars = new Scalar[tensors.length];
      int end = tensors.length - 1;
      recur(0, end);
      scalars[end] = epsilon.zero();
      if (0 < end)
        list.add(end);
    }

    private void recur(int beg, int end) {
      Scalar max = epsilon.zero();
      scalars[beg] = max;
      if (beg + 1 < end) { // at least one element in between beg and end
        LieGroupElement lieGroupElement = lieGroup.element(tensors[beg]).inverse();
        Tensor normal = NORMALIZE_UNLESS_ZERO.apply(tangent.apply(lieGroupElement.combine(tensors[end])));
        int mid = -1;
        for (int index = beg + 1; index < end; ++index) {
          Tensor vector = tangent.apply(lieGroupElement.combine(tensors[index]));
          Scalar dist = Norm._2.ofVector(vector.subtract(vector.dot(normal).pmul(normal)));
          scalars[index] = dist;
          if (Scalars.lessThan(max, dist)) {
            max = dist;
            mid = index;
          }
        }
        if (Scalars.lessThan(epsilon, max)) {
          recur(beg, mid);
          recur(mid, end);
          return;
        }
      }
      list.add(beg);
    }

    @Override // from Result
    public Tensor result() {
      return Tensor.of(list.stream() //
          .map(i -> tensors[i]) //
          .map(Tensor::copy));
    }

    @Override // from Result
    public Tensor errors() {
      return Tensors.of(scalars);
    }
  }
}
