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
   * @param epsilon
   * @return */
  public static TensorUnaryOperator of(LieGroup lieGroup, TensorUnaryOperator tangent, Scalar epsilon) {
    return new CurveDecimation(lieGroup, tangent, epsilon);
  }

  // ---
  private final LieGroup lieGroup;
  private final TensorUnaryOperator tangent;
  private final Scalar epsilon2;

  private CurveDecimation(LieGroup lieGroup, TensorUnaryOperator tangent, Scalar epsilon) {
    this.lieGroup = lieGroup;
    this.tangent = tangent;
    this.epsilon2 = Sign.requirePositiveOrZero(epsilon).multiply(epsilon);
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
    LieGroupElement lieGroupElement = lieGroup.element(first).inverse();
    Tensor normal = NORMALIZE_UNLESS_ZERO.apply(tangent.apply(lieGroupElement.combine(last)));
    Scalar dmax = epsilon2.zero();
    int skip = -1;
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor vector = tangent.apply(lieGroupElement.combine(tensor.get(index)));
      Scalar dist = Norm2Squared.ofVector(vector.subtract(normal.multiply(normal.dot(vector).Get())));
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
