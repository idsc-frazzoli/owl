// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** @param sequence of (x,y,z) points in He(3) and weights non-negative and normalized
 * @return associated biinvariant meanb which is the solution to the barycentric equation
 * Execption is thrown if
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p32
 * Vincent Arsigny — Xavier Pennec — Nicholas Ayache */
public enum HeBiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  private final static Scalar TWO = RealScalar.of(2);

  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    VectorQ.requireLength(sequence.get(0), 3);
    Scalar xMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(0))));
    Scalar yMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(1))));
    Scalar zMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(2))));
    Scalar xyMean = (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.Get(0).multiply(xyz.Get(1)))));
    zMean = zMean.add(xMean.multiply(yMean).subtract(xyMean).divide(TWO));
    return Tensors.of(xMean, yMean, zMean);
  }
}
