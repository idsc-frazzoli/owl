// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

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
  private final static Scalar ZERO = RealScalar.ZERO;
  private final static Scalar ONE = RealScalar.ONE;
  private final static Scalar TWO = RealScalar.of(2);

  private static Tensor M(Tensor alpha) {
    // TODO OB: for alpha = 0 I use limit case limes(M(n)) n->0 which is basically wrong since we take exactly zero BUT
    // the result agrees with the paper
    if (alpha.equals(RealScalar.ZERO)) {
      return Tensors.matrix(new Scalar[][] { //
          { ONE, ZERO }, //
          { ZERO, ONE } });
    } else {
      Scalar angle = (Scalar) alpha;
      Scalar m11 = angle.multiply(Sin.FUNCTION.apply(angle)).divide(TWO.multiply(RealScalar.ONE.subtract(Cos.FUNCTION.apply(angle))));
      Scalar m12 = angle.divide(TWO);
      return Tensors.matrix(new Scalar[][] { //
          { m11, m12 }, //
          { m12.negate(), m11 } });
    }
  }

  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    Tensor a1 = sequence.get(0).get(2);
    Tensor amean = a1.add(weights.dot(Tensor.of(sequence.stream().map(xya -> a1.negate().add(xya.get(2))))));
    Tensor Z = weights.dot(Tensor.of(sequence.stream().map(xya -> M(amean.subtract(xya.get(2))))));
    Tensor tmean = weights.dot(//
        Tensor.of(sequence.stream().map(//
            xya -> Inverse.of(Z).dot(M(amean.subtract(xya.get(2)))).dot(Transpose.of(RotationMatrix.of(xya.Get(2)))).dot(xya.extract(0, 2)))));
    return Tensors.of(tmean.Get(0), tmean.Get(1), amean);
  }
}
