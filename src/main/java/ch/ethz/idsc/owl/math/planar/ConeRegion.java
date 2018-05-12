// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Sign;

/** planar infinite cone region */
public class ConeRegion implements RegionWithDistance<Tensor>, Serializable {
  private static final Scalar PI_HALF = RealScalar.of(Math.PI / 2);
  // ---
  private final Tensor apex;
  private final TensorUnaryOperator inverse;
  private final Scalar semi;
  private final Scalar semi_pi_half;
  private final Tensor normal;

  /** @param xya vector of the form {x, y, angle} where {x, y} is the apex of the cone, and
   * angle aligns with the center line of the cone
   * @param semi half angular width of cone,
   * for instance semi==pi/4 corresponds to a cone with a right angle */
  public ConeRegion(Tensor xya, Scalar semi) {
    apex = xya;
    inverse = new Se2Bijection(xya).inverse();
    this.semi = Sign.requirePositiveOrZero(semi);
    semi_pi_half = semi.add(PI_HALF);
    normal = AngleVector.of(semi_pi_half);
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor tensor) {
    Tensor local = inverse.apply(tensor);
    local.set(Scalar::abs, 1); // normalize y coordinate
    Scalar angle = ArcTan.of(local.Get(0), local.Get(1)); // non-negative
    return Scalars.lessThan(angle, semi);
  }

  @Override // from DistanceFunction<Tensor>
  public Scalar distance(Tensor tensor) {
    Tensor local = inverse.apply(tensor);
    local.set(Scalar::abs, 1); // normalize y coordinate
    Scalar angle = ArcTan.of(local.Get(0), local.Get(1)); // non-negative
    if (Scalars.lessThan(angle, semi))
      return RealScalar.ZERO;
    return Scalars.lessThan(angle, semi_pi_half) //
        ? normal.dot(local).Get()
        : Norm._2.ofVector(local);
  }

  /** @return {x, y, angle} */
  public Tensor apex() {
    return apex;
  }

  /** @return half angular width of cone */
  public Scalar semi() {
    return semi;
  }
}
