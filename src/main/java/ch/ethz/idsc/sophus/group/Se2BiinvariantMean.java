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
  private final static Scalar TWO = RealScalar.of(2);

  private static Tensor M(Tensor vector) {
    Scalar angle = vector.Get(0);
    Scalar m11 = angle.multiply(Sin.FUNCTION.apply(angle)).divide(TWO.multiply(RealScalar.ONE.subtract(Cos.FUNCTION.apply(angle))));
    Scalar m12 = angle.divide(TWO);
    return Tensors.matrix(new Scalar[][] { //
        { m11, m12.negate() }, //
        { m12, m11 } });
  }

  public Tensor mean(Tensor sequence, Tensor weights) {
    Tensor R1 = RotationMatrix.of(sequence.get(0).Get(2));
    Tensor Rmean = R1.dot(So2Exponential.INSTANCE.exp(weights.dot(Tensor.of(//
        sequence.stream().map(xya -> So2Exponential.INSTANCE.log(Transpose.of(R1).dot(RotationMatrix.of(xya.Get(2)))))))));
    Scalar aMean = So2Exponential.INSTANCE.log(Rmean).Get(0);
    // The angle is wrong by exactly pi/2 => find out what has to be infront of the + sign
    // ==================================
    // // --- Calculation of Tmean version 1: fewer lines
    // ==================================
    Tensor Z = weights.dot(Tensor.of(sequence.stream().map(xya -> M(So2Exponential.INSTANCE.log(Rmean.dot(Transpose.of(RotationMatrix.of(xya.Get(2)))))))));
    Tensor temp1 = Tensor.of(sequence.stream().map(xya -> M(So2Exponential.INSTANCE.log(Rmean.dot(RotationMatrix.of(xya.Get(2)))))
        .dot(Transpose.of(RotationMatrix.of(xya.Get(2)))).dot(xya.extract(0, 2))));
    Tensor Tmean1 = weights.dot(temp1.dot(Inverse.of(Z)));
    // ==================================
    // --- Calculation of Tmean Version 2: step by step
    // ==================================
    Tensor summands = Tensors.empty();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor RiT = Transpose.of(RotationMatrix.of(sequence.get(index).Get(2)));
      Tensor ti = sequence.get(index).extract(0, 2);
      Tensor temp = M(So2Exponential.INSTANCE.log(Rmean.dot(RiT)));
      temp = temp.dot(RiT).dot(ti);
      temp = Inverse.of(Z).dot(temp);
      summands.append(temp);
    }
    Tensor Tmean = weights.dot(summands);
    return Tensors.of(Tmean.Get(0), Tmean.Get(1), aMean);
  }
}
