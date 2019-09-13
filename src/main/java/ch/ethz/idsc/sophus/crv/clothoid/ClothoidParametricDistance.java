// code by gjoel
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** length of clothoid in Euclidean plane */
public enum ClothoidParametricDistance implements TensorMetric, TensorNorm {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return distance(new Clothoid(p, q));
  }

  /** @param clothoid
   * @return curve length of given clothoid */
  public static Scalar distance(Clothoid clothoid) {
    return clothoid.new Curve().length();
  }

  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}
