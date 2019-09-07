// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.stream.Stream;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;
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
    return tensor.length() < 3 //
        ? MatrixQ.require(tensor).copy()
        : recur(tensor);
  }

  private Tensor recur(Tensor tensor) {
    if (tensor.length() == 2)
      return tensor;
    Tensor first = tensor.get(0);
    Tensor last = Last.of(tensor);
    LieGroupElement lieGroupElement = lieGroup.element(first).inverse();
    Tensor normal = NORMALIZE_UNLESS_ZERO.apply(tangent.apply(lieGroupElement.combine(last)));
    Scalar dmax = epsilon2.zero();
    int skip = -1;
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor vector = tangent.apply(lieGroupElement.combine(tensor.get(index)));
      Scalar dist = Norm2Squared.ofVector(vector.subtract(normal.dot(vector).pmul(normal)));
      if (Scalars.lessThan(dmax, dist)) {
        dmax = dist;
        skip = index;
      }
    }
    if (Scalars.lessThan(epsilon2, dmax)) {
      Tensor lo = recur(Tensor.of(tensor.stream().limit(skip + 1)));
      Tensor hi = recur(Tensor.of(tensor.stream().skip(skip)));
      return Tensor.of(Stream.concat( //
          lo.stream().limit(lo.length() - 1), //
          hi.stream()));
    }
    return Unprotect.byRef(first, last);
  }
}
