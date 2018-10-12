// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Important: the use of this variant of the six point scheme is not recommended
 * Instead, use {@link SixPointCurveSubdivision}
 * 
 * refinement according to
 * Dyn/Sharon 2014: Manifold-valued subdivision schemes based on geodesic inductive averaging
 * 
 * Dyn/Sharon 2014 p.20 show for this split a contractivity factor of mu = 0.9844
 * 
 * weights = {3, -25, 150, 150, -25, 3}/256;
 * {b (1 - a), 1 - b, a b, a b, 1 - b, b (1 - a)}/2
 * Solve[Thread[% == weights]] */
public class FarSixPointCurveSubdivision extends AbstractSixPointCurveSubdivision {
  private static final Scalar PR = RationalScalar.of(50, 51);
  private static final Scalar Q_ = RationalScalar.of(153, 128);

  // ---
  public FarSixPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from AbstractSixPointCurveSubdivision
  protected Tensor center(Tensor p, Tensor q, Tensor r, Tensor s, Tensor t, Tensor u) {
    Tensor pr = geodesicInterface.split(p, r, PR);
    Tensor q_ = geodesicInterface.split(q, pr, Q_);
    Tensor us = geodesicInterface.split(u, s, PR);
    Tensor t_ = geodesicInterface.split(t, us, Q_);
    return geodesicInterface.split(q_, t_, RationalScalar.HALF);
  }
}
