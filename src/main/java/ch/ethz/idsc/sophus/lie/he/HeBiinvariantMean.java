// code by ob
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @param sequence of (x,y,z) points in He(n) of shape ((x1, ... , xm),(y1, ... , ym),z)
 * @param normalized non negative weights
 * @return associated biinvariant mean which is the solution to the barycentric equation
 * Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p32
 * Vincent Arsigny - Xavier Pennec - Nicholas Ayache */
public enum HeBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private final static Scalar TWO = RealScalar.of(2);

  @Override // from BiinvariantMean
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    // ---
    Tensor x = sequence.get(Tensor.ALL, 0);
    Tensor y = sequence.get(Tensor.ALL, 1);
    Tensor xMean = weights.dot(x);
    Tensor yMean = weights.dot(y);
    Scalar dot = xMean.pmul(yMean).flatten(-1).map(Scalar.class::cast).reduce(Scalar::add).get();
    Scalar zMean = (Scalar) weights.dot(sequence.get(Tensor.ALL, 2));
    Scalar xyMean = sequence.get(0).get(0) instanceof Scalar //
        ? (Scalar) weights.dot(x.pmul(y))
        : (Scalar) weights.dot(Tensor.of(sequence.stream().map(xyz -> xyz.get(0).dot(xyz.get(1)))));
    zMean = zMean.add(dot.subtract(xyMean).divide(TWO));
    return Tensors.of(xMean, yMean, zMean);
  }
}
