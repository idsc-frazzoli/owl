// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.ArcCos;
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
public enum Se2BiinvariantMean {
  INSTANCE;
  // ---
  private final static Scalar TWO = RealScalar.of(2);
  private final static Scalar ZERO = RealScalar.ZERO;

  //
  private static Tensor M(Tensor so2) {
    // TODO OB: maybe get(0).Get(1).negate
    Scalar angle = so2.get(0).Get(1);
    Scalar m11 = angle.multiply(Sin.FUNCTION.apply(angle)).divide(TWO.multiply(RealScalar.ONE.subtract(Cos.FUNCTION.apply(angle))));
    Scalar m12 = angle.divide(TWO);
    return Tensors.matrix(new Scalar[][] { //
        { m11, m12 }, //
        { m12.negate(), m11 } });
  }

  public static Tensor mean(Tensor sequence, Tensor weights) {
    // Formula 20 p38
    Tensor R1 = RotationMatrix.of(sequence.get(0).Get(2));
    // TODO OB/JPH: there has to be something infront of the '+' sign otherwise Rmean is wrong.
    // ====== Method one to calculate Rmean
    Tensor sum = RotationMatrix.of(ZERO);
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor result = So2Exponential.INSTANCE.log(Transpose.of(R1).pmul(RotationMatrix.of(sequence.get(index).Get(2)))).multiply(weights.Get(index));
      sum = sum.add(result);
    }
    // Tensor Rmean = R1.pmul(So2Exponential.INSTANCE.exp(sum));
    // ====== Method two to calculate Rmean
    Tensor exponent = weights.dot(Tensor.of(sequence.stream().map(xya -> So2Exponential.INSTANCE.log(Transpose.of(R1).pmul(RotationMatrix.of(xya.Get(2)))))));
    Tensor Rmean = R1.pmul(So2Exponential.INSTANCE.exp(exponent));
    Tensor Z = weights.dot(Tensor.of(sequence.stream().map(xya -> M(So2Exponential.INSTANCE.log(Rmean.pmul(Transpose.of(RotationMatrix.of(xya.Get(2)))))))));
    // calculatr tmean;
    Tensor tmean = Tensors.vector(0, 0);
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor RiT = Transpose.of(RotationMatrix.of(sequence.get(index).Get(2)));
      Tensor temp = Inverse.of(Z).pmul(M(So2Exponential.INSTANCE.log(Rmean.pmul(Transpose.of(RiT))))).pmul(RiT);
      temp = sequence.get(index).extract(0, 2).dot(temp);
      tmean = tmean.add(temp);
    }
    return Tensors.of(tmean.Get(0), tmean.Get(1), ArcCos.FUNCTION.apply(Rmean.get(0).Get(0)));
  }

  public static void main(String[] args) {
    // control points that is used in the paper
    Double root = Math.sin(Math.PI / 4);
    Tensor p = Tensors.vector(-root, root, Math.PI / 4);
    Tensor q = Tensors.vector(0, 2 * root, 0);
    Tensor r = Tensors.vector(-root, -root, -Math.PI / 4);
    // weights not mentioned in the paper
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    Tensor expected = Tensors.vector(0, 0.2171, 0);
    System.out.println(mean(Tensors.of(p, q, r), weights));
  }
}
