// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sign;

/** @param sequence of (x,y,z) points in He(n) of shape ((x1, ... , xm),(y1, ... , ym),z)
 * @param normalized non negative weights
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p32
 * Vincent Arsigny — Xavier Pennec — Nicholas Ayache */
public enum HeBiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private final static Scalar TWO = RealScalar.of(2);

  private static void checkAffinity(Tensor weights) {
    if (!Total.of(weights).equals(RealScalar.ONE))
      System.out.println("Application of Biinvariant mean not valid! (sum of weights not 1");
    weights.stream().map(w -> Sign.requirePositiveOrZero((Scalar) w));
  }

  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    checkAffinity(weights);
    if (sequence.get(0).get(0) instanceof Scalar) {
      Scalar xMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(0))));
      Scalar yMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(1))));
      Scalar zMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(2))));
      Scalar xyMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(0).multiply(xyz.Get(1)))));
      zMean = zMean.add(xMean.multiply(yMean).subtract(xyMean).divide(TWO));
      return Tensors.of(xMean, yMean, zMean);
    }
    Tensor xMean = weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.get(0))));
    Tensor yMean = weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.get(1))));
    Scalar zMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(2))));
    Scalar xyMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.get(0).dot(xyz.get(1)))));
    zMean = zMean.add(xMean.dot(yMean).subtract(xyMean).divide(TWO));
    return Tensors.of(xMean, yMean, zMean);
  }
}
