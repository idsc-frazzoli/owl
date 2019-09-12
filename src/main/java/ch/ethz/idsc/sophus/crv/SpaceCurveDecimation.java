// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see CurveDecimation */
/* package */ class SpaceCurveDecimation implements CurveDecimation {
  private final LineDistance lineDistance;
  private final Scalar epsilon;

  /** @param lieGroup
   * @param log
   * @param epsilon */
  public SpaceCurveDecimation(LineDistance lineDistance, Scalar epsilon) {
    this.lineDistance = lineDistance;
    this.epsilon = epsilon;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return evaluate(tensor).result();
  }

  @Override // from CurveDecimation
  public Result evaluate(Tensor tensor) {
    return Tensors.isEmpty(tensor) //
        ? EmptyResult.INSTANCE
        : new SpaceResult(tensor);
  }

  private class SpaceResult implements Result, Serializable {
    private final Tensor[] tensors;
    private final Scalar[] scalars;
    private final List<Integer> list = new LinkedList<>();

    /** @param tensor of length at least 1 */
    public SpaceResult(Tensor tensor) {
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
        TensorNorm tensorNorm = lineDistance.tensorNorm(tensors[beg], tensors[end]);
        int mid = -1;
        for (int index = beg + 1; index < end; ++index) {
          Scalar dist = tensorNorm.norm(tensors[index]);
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
      return Tensor.of(list.stream().map(i -> tensors[i]).map(Tensor::copy));
    }

    @Override // from Result
    public Tensor errors() {
      return Tensors.of(scalars);
    }
  }
}
