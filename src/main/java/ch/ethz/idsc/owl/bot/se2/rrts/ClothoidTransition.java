// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid.Curvature;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidTransition extends AbstractTransition {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  /** @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @return */
  public static ClothoidTransition of(Tensor start, Tensor end) {
    return new ClothoidTransition(start, end, new Clothoid(start, end));
  }

  /***************************************************/
  private final Clothoid clothoid;

  private ClothoidTransition(Tensor start, Tensor end, Clothoid clothoid) {
    super(start, end, clothoid.length());
    this.clothoid = clothoid;
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
    Sign.requirePositive(minResolution);
    return Subdivide.of(_0, _1, //
        Math.max(1, Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue())).map(clothoid);
  }

  public Clothoid clothoid() {
    return clothoid;
  }

  public Curvature curvature() {
    return clothoid.new Curvature();
  }
}
