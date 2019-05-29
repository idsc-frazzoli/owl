// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.AffinityQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Tan;

/** @param sequence of (x,y,a) points in SE(2)and weights non-negative and normalized
 * rotation angles ai have to satisfy: sup (i,j) |ai- aj| <= pi - C
 * 
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * Despite no existing biinvariant for general rigid motion for the 2D case an explicit solution exists
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p38
 * Vincent Arsigny - Xavier Pennec - Nicholas Ayache
 * Source for Constant C: https://hal.inria.fr/inria-00073318/
 * Xavier Pennec. */
public enum Se2BiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private static Tensor M(Scalar angle) {
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
    checkValidity(sequence);
    AffinityQ.requirePositive(weights);
    Scalar a1 = sequence.get(0).Get(2);
    Scalar amean = a1.add(weights.dot(Tensor.of(sequence.stream().map(xya -> a1.negate().add(xya.get(2))))));
    Tensor invZ = Inverse.of(weights.dot(Tensor.of(sequence.stream().map(xya -> M(amean.subtract(xya.get(2)))))));
    Tensor tmean = weights.dot(Tensor.of(sequence.stream().map( //
        xya -> invZ.dot(M(amean.subtract(xya.get(2))).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
    return tmean.append(amean);
  }

  // TODO this function could benefit from a comment for intuition
  private static void checkValidity(Tensor sequence) {
    // TODO OB: find a useful constant => Paper by Pennec?
    Scalar C = RealScalar.of(0.01);
    Scalar supremum = Pi.VALUE.subtract(C);
    Tensor angles = sequence.get(Tensor.ALL, 2);
    Scalar minAngle = angles.stream().reduce(Min::of).get().Get();
    Scalar maxAngle = angles.stream().reduce(Max::of).get().Get();
    if (Scalars.lessThan(supremum, Abs.FUNCTION.apply(minAngle.subtract(maxAngle)))) {
      System.out.println("Application of Biinvariant mean not valid! (angle distance >= pi-c)");
      throw TensorRuntimeException.of(supremum, minAngle, maxAngle);
    }
  }
}
