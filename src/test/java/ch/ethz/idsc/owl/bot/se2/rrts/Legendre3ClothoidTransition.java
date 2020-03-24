// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.math.IntegerLog2;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/** gjoel used a transition based on subdivision
 * 
 * DECOMMISSIONED */
/* package */ class Legendre3ClothoidTransition extends AbstractTransition {
  public static final CurveSubdivision CURVE_SUBDIVISION = LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1);

  /** @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @return */
  public static Legendre3ClothoidTransition of(Tensor start, Tensor end) {
    return new Legendre3ClothoidTransition(start, end, new Clothoid(start, end));
  }

  /***************************************************/
  private Legendre3ClothoidTransition(Tensor start, Tensor end, Clothoid curve) {
    super(start, end, curve.length());
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return Drop.head(linearized(minResolution), 1);
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue();
    Tensor samples = linearized(length().divide(RealScalar.of(steps)));
    return new TransitionWrap( //
        Drop.head(samples, 1), //
        Distances.of(ClothoidParametricDistance.INSTANCE, samples));
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    /* investigation has shown that midpoint splits result in clothoid segments of approximately equal length */
    return Nest.of(CURVE_SUBDIVISION::string, Unprotect.byRef(start(), end()), //
        IntegerLog2.ceiling(Math.max(1, Ceiling.of(length().divide(Sign.requirePositive(minResolution))).number().intValue())));
  }

  public HeadTailInterface terminalRatios() {
    return new Clothoid(start(), end()).new Curvature();
  }
}
