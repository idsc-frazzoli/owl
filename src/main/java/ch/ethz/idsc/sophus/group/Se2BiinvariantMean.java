// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Tan;

/** @param sequence of (x, y, a) points in SE(2) and weights non-negative and normalized
 * rotation angles ai have to satisfy: sup (i,j) |ai-aj| <= pi - C
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * 
 * For the rigid motion in 2D an explicit solution for the biinvariant mean exists.
 * 
 * Reference:
 * "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p.38
 * Vincent Arsigny - Xavier Pennec - Nicholas Ayache
 * Source for Constant C: https://hal.inria.fr/inria-00073318/
 * Xavier Pennec. */
public enum Se2BiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  private static final Scalar C = RealScalar.of(0.01);

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

  /** The biinvariant mean for 2D rigid transformation exists only in the absence of antipodal points.
   * 
   * @param angles of elements in SE(2)
   * @return whether the maximum rotation between any two elements is less than pi => no antipodal points */
  private static boolean isRegular(Tensor angles) {
    ScalarSummaryStatistics scalarSummaryStatistics = angles.stream() //
        .map(Scalar.class::cast) //
        .collect(ScalarSummaryStatistics.collector());
    Scalar min = scalarSummaryStatistics.getMin();
    Scalar max = scalarSummaryStatistics.getMax();
    return Scalars.lessThan(max.subtract(min), Pi.VALUE.subtract(C));
  }

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    // TODO OB: interestingly the filtering performance is lowered when checking this condition
    // because the spots where the condition actually fails are filtered well
    boolean isRegular = isRegular(sequence.get(Tensor.ALL, 2));
    // FIXME OB: This is a ugly fix to make the data exportable.
    // Using the assumption that the angles are all close
    Scalar a1 = sequence.get(0).Get(2);
    if (isRegular) {
      Scalar amean = a1.subtract(weights.dot(sequence.get(Tensor.ALL, 2).map(a1::subtract)));
      // make transformation s.t. mean rotation is zero and retransformation after taking mean
      Se2GroupElement transfer = new Se2GroupElement(Tensors.of(ZERO, ZERO, amean));
      Tensor transferred = Tensor.of(sequence.stream().map(transfer.inverse()::combine));
      Tensor invZ = Inverse.of(weights.dot(transferred.get(Tensor.ALL, 2).negate().map(Se2BiinvariantMean::M)));
      Tensor tmean = weights.dot(Tensor.of(transferred.stream().map( //
          xya -> invZ.dot(M(xya.Get(2).negate()).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
      return transfer.combine(tmean.append(ZERO));
    }
    Se2GroupElement preTransfer = new Se2GroupElement(Tensors.of(ZERO, ZERO, Pi.VALUE));
    Tensor sequencePreTransfered = Tensor.of(sequence.stream().map(xya -> preTransfer.inverse().combine(xya)));
    Scalar amean = a1.add(weights.dot(Tensor.of(sequencePreTransfered.stream().map(xya -> a1.negate().add(xya.Get(2))))));
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    Se2GroupElement transfer = new Se2GroupElement(Tensors.of(ZERO, ZERO, amean));
    Tensor transferred = Tensor.of(sequencePreTransfered.stream().map(transfer.inverse()::combine));
    Tensor invZ = Inverse.of(weights.dot(Tensor.of(transferred.stream().map(xya -> M(xya.Get(2).negate())))));
    Tensor tmean = weights.dot(Tensor.of(transferred.stream().map( //
        xya -> invZ.dot(M(xya.Get(2).negate()).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
    return preTransfer.combine(transfer.combine(tmean.append(ZERO)));
  }
}
