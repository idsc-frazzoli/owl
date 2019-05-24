// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Tan;

/** @param sequence of (x,y,a) points in SE(2)and weights non-negative and normalized
 * rotation angles ai have to satisfy: sup (i,j) |ai- aj| <= pi - C
 * 
 * @return associated biinvariant meanb which is the solution to the barycentric equation
 * Despite no existing biinvariant for general rigid motion for the 2D case an explicit soluvtion exists
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p38
 * Vincent Arsigny — Xavier Pennec — Nicholas Ayache
 * Source for Constant C: https://hal.inria.fr/inria-00073318/
 * Xavier Pennec. */
public enum Se2BiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private static Tensor M(Scalar angle) {
    // TODO OB: for alpha = 0 I use limit case limes(M(n)) n->0 which is basically wrong since we take exactly zero BUT
    // the result agrees with the paper
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
    Scalar a1 = sequence.get(0).Get(2);
    Scalar amean = a1.add(weights.dot(Tensor.of(sequence.stream().map(xya -> a1.negate().add(xya.get(2))))));
    Tensor invZ = Inverse.of(weights.dot(Tensor.of(sequence.stream().map(xya -> M(amean.subtract(xya.get(2)))))));
    Tensor tmean = weights.dot(Tensor.of(sequence.stream().map( //
        xya -> invZ.dot(M(amean.subtract(xya.get(2))).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
    return tmean.append(amean);
  }
}
