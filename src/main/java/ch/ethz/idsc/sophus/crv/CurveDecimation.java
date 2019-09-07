// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sign;

/** Generalization of the Ramer-Douglas-Peucker algorithm
 * 
 * Quote: "The Ramer-Douglas-Peucker algorithm decimates a curve
 * composed of line segments to a similar curve with fewer points.
 * [...] The algorithm defines 'dissimilar' based on the maximum
 * distance between the original curve and the simplified curve."
 * 
 * https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm */
public class CurveDecimation implements TensorUnaryOperator {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Norm._2);

  /** @param lieGroup
   * @param tangent mapper
   * @param dimensions of lie group
   * @param epsilon
   * @return */
  public static TensorUnaryOperator of(LieGroup lieGroup, TensorUnaryOperator tangent, int dimensions, Scalar epsilon) {
    return new CurveDecimation(lieGroup, tangent, dimensions, epsilon);
  }

  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator tangent;
  private final Tensor eye;
  private final Scalar epsilon;

  private CurveDecimation(LieGroup lieGroup, TensorUnaryOperator tangent, int dimensions, Scalar epsilon) {
    this.lieGroup = lieGroup;
    this.tangent = tangent;
    eye = IdentityMatrix.of(dimensions);
    this.epsilon = Sign.requirePositiveOrZero(epsilon);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    return tensor.length() < 3 //
        ? MatrixQ.require(tensor).copy()
        : recur(tensor);
  }

  // helper function
  private Tensor recur(Tensor tensor) {
    if (tensor.length() == 2)
      return tensor;
    Tensor first = tensor.get(0);
    Tensor last = Last.of(tensor);
    Tensor vector = NORMALIZE_UNLESS_ZERO.apply(tangent.apply(lieGroup.element(first).inverse().combine(last)));
    Tensor projection = eye.subtract(TensorProduct.of(vector, vector));
    Scalar dmax = epsilon.zero();
    int split = -1;
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor lever = tangent.apply(lieGroup.element(first).inverse().combine(tensor.get(index)));
      Scalar dist = Norm._2.ofVector(projection.dot(lever));
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
