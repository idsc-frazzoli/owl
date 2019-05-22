// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;

/** @param sequence of (x,y,z) points in He(n) and weights non-negative and normalized
 * @return associated biinvariant meanb which is the solution to the barycentric equation
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p32
 * Vincent Arsigny — Xavier Pennec — Nicholas Ayache */
public enum HeBiinvariantMean {
  INSTANCE;
  // ---
  private final static Scalar TWO = RealScalar.of(2);

  public static Tensor mean(Tensor sequence, Tensor weights) {
    Tensor xMean = Total.of(weights.pmul(Tensor.of(sequence.stream().map(xyz -> xyz.get(0)))));
    Tensor yMean = Total.of(weights.pmul(Tensor.of(sequence.stream().map(xyz -> xyz.get(1)))));
    Tensor zMean = Total.of(weights.pmul(Tensor.of(sequence.stream().map(xyz -> xyz.get(1)))));
    zMean = zMean.add(xMean.pmul(yMean).divide(TWO));
    zMean = zMean.subtract(Total.of(weights.pmul(Tensor.of(sequence.stream().map(xyz -> xyz.get(0).pmul(xyz.get(1)))))).divide(TWO));
    return Tensors.of(xMean, yMean, zMean);
  }
}
