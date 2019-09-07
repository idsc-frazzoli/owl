// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** @see CurveDecimation */
/* package */ class CurveDecimationLieGroup implements TensorUnaryOperator {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);
  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator tangent;
  private final Scalar epsilon2;

  public CurveDecimationLieGroup(LieGroup lieGroup, TensorUnaryOperator tangent, Scalar epsilon) {
    this.lieGroup = lieGroup;
    this.tangent = tangent;
    this.epsilon2 = epsilon.multiply(epsilon);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    return tensor.length() <= 2 //
        ? MatrixQ.require(tensor).copy()
        : new Decimation(tensor).result();
  }

  private class Decimation {
    private final Tensor[] tensors;
    private final List<Integer> list = new LinkedList<>();

    /** @param tensor of length at least 2 */
    public Decimation(Tensor tensor) {
      tensors = tensor.stream().toArray(Tensor[]::new);
      int end = tensors.length - 1;
      recur(0, end);
      list.add(end);
    }

    private void recur(int beg, int end) {
      if (beg + 1 < end) {
        LieGroupElement lieGroupElement = lieGroup.element(tensors[beg]).inverse();
        Tensor normal = NORMALIZE_UNLESS_ZERO.apply(tangent.apply(lieGroupElement.combine(tensors[end])));
        Scalar dmax = epsilon2.zero();
        int mid = -1;
        for (int index = beg + 1; index < end - 1; ++index) {
          Tensor vector = tangent.apply(lieGroupElement.combine(tensors[index]));
          Scalar dist = Norm2Squared.ofVector(vector.subtract(normal.dot(vector).pmul(normal)));
          if (Scalars.lessThan(dmax, dist)) {
            dmax = dist;
            mid = index;
          }
        }
        if (Scalars.lessThan(epsilon2, dmax)) {
          recur(beg, mid);
          recur(mid, end);
          return;
        }
      }
      list.add(beg);
    }

    public Tensor result() {
      return Tensor.of(list.stream() //
          .map(i -> tensors[i]) //
          .map(Tensor::copy));
    }
  }
}
