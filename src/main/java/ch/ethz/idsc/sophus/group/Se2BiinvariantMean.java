// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Tan;

/** @param sequence of (x, y, a) points in SE(2) and weights non-negative and normalized
 * rotation angles a_i have to satisfy: sup (i,j) |ai-aj| <= pi - C
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * 
 * For the rigid motion in 2D an explicit solution for the biinvariant mean exists.
 * 
 * Reference:
 * "Bi-invariant Means in Lie Groups. Application to left-invariant Polyaffine Transformations." p.38
 * Vincent Arsigny, Xavier Pennec, Nicholas Ayache */
public enum Se2BiinvariantMean implements BiinvariantMean {
  DEFAULT(So2DefaultBiinvariantMean.INSTANCE), //
  ARSIGNY(So2ArsignyBiinvariantMean.INSTANCE), //
  ;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---
  private final So2BiinvariantMean so2BiinvariantMean;

  private Se2BiinvariantMean(So2BiinvariantMean so2BiinvariantMean) {
    this.so2BiinvariantMean = so2BiinvariantMean;
  }

  /** @param angle
   * @return matrix of dimensions 2 x 2 */
  static Tensor M(Scalar angle) {
    if (Scalars.isZero(angle))
      return IdentityMatrix.of(2);
    // ---
    Scalar m12 = angle.multiply(RationalScalar.HALF);
    Scalar m11 = m12.divide(Tan.FUNCTION.apply(m12));
    return Tensors.matrix(new Scalar[][] { //
        { m11, m12 }, //
        { m12.negate(), m11 } });
  }

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    // AffineQ is checked in So2BiinvariantMean
    Scalar amean = so2BiinvariantMean.mean(sequence.get(Tensor.ALL, 2), weights);
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    Se2GroupElement transfer = new Se2GroupElement(Tensors.of(ZERO, ZERO, amean));
    Tensor transferred = Tensor.of(sequence.stream().map(transfer.inverse()::combine));
    Tensor invZ = Inverse.of(weights.dot(transferred.get(Tensor.ALL, 2).negate().map(Se2BiinvariantMean::M)));
    Tensor tmean = weights.dot(Tensor.of(transferred.stream().map( //
        xya -> invZ.dot(M(xya.Get(2).negate()).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
    return transfer.combine(tmean.append(ZERO));
  }
}
