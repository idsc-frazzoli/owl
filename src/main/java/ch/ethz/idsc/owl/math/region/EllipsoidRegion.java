// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Sign;

/** EllipsoidRegion implements an axis aligned elliptic region in the vector space R^n.
 * 
 * The region also finds applications for other spaces, such as R^n x R^m
 * where axis depended scaling is desired. One use case is the Lotka-Volterra model.
 * 
 * Notice: evaluate(...) does not correspond to Euclidean distance
 * 
 * @see BallRegion */
public class EllipsoidRegion extends ImplicitFunctionRegion implements Serializable {
  private final Tensor center;
  private final Tensor radius;
  private final Tensor invert;

  /** The components of radius have to be strictly positive.
   * For a radius equals to zero we would run into numerical trouble:
   * distance Inf when outside, and NaN when at the center
   * 
   * @param center of the ellipsoid
   * @param radius of the different axes with same number of entries as center
   * all components of radius must be strictly positive.
   * if a component of radius is DoubleScalar.POSITIVE_INFINITY, this corresponds to a cylinder
   * @see BallRegion */
  public EllipsoidRegion(Tensor center, Tensor radius) {
    // assert that radius are strictly positive
    if (radius.stream().map(Scalar.class::cast).anyMatch(Sign::isNegativeOrZero))
      throw TensorRuntimeException.of(radius);
    // ---
    this.center = VectorQ.requireLength(center, radius.length()).copy();
    this.radius = radius.copy();
    invert = radius.map(Scalar::reciprocal);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor tensor) {
    return Vector2NormSquared.of(tensor.subtract(center).pmul(invert)).subtract(RealScalar.ONE);
  }

  public Tensor center() {
    return center.unmodifiable();
  }

  public Tensor radius() {
    return radius.unmodifiable();
  }
}
