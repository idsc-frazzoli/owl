// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;

/** Quote from Wikipedia:
 * The algorithm is widely used in robotics to perform simplification and denoising
 * of range data acquired by a rotating range scanner.
 * In this field it is known as the split-and-merge algorithm and is attributed to Duda and Hart.
 * 
 * The expected complexity of this algorithm is O(n log n).
 * However, the worst-case complexity is O(n^2). */
/* package */ class RamerDouglasPeucker implements TensorUnaryOperator {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);

  /** @param epsilon
   * @return operator that takes as input tensors of dimensions N x 2 */
  public static TensorUnaryOperator of(Scalar epsilon) {
    return new RamerDouglasPeucker(epsilon);
  }

  // ---
  private final Scalar epsilon;

  private RamerDouglasPeucker(Scalar epsilon) {
    this.epsilon = Sign.requirePositiveOrZero(epsilon);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    if (Unprotect.dimension1(tensor) == 2)
      return tensor.length() < 3 //
          ? MatrixQ.require(tensor).copy()
          : recur(tensor);
    throw TensorRuntimeException.of(tensor);
  }

  // helper function
  private Tensor recur(Tensor tensor) {
    if (tensor.length() == 2)
      return tensor;
    Tensor first = tensor.get(0);
    Tensor last = Last.of(tensor);
    Tensor vector = NORMALIZE_UNLESS_ZERO.apply(last.subtract(first));
    Tensor cross2 = Tensors.of(vector.Get(1).negate(), vector.Get(0));
    Scalar dmax = epsilon.zero();
    int split = -1;
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor lever = tensor.get(index).subtract(first);
      Scalar dist = lever.dot(cross2).Get().abs();
      if (Scalars.lessThan(dmax, dist)) {
        dmax = dist;
        split = index;
      }
    }
    if (Scalars.lessThan(epsilon, dmax)) {
      Tensor lo = recur(tensor.extract(0, split + 1));
      Tensor hi = recur(tensor.extract(split, tensor.length()));
      return Join.of(lo.extract(0, lo.length() - 1), hi);
    }
    return Tensors.of(first, last);
  }
}
