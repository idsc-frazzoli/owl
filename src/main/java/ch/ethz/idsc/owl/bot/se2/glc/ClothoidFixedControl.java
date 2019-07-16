// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuit;
import ch.ethz.idsc.owl.math.pursuit.CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** clothoid pursuit control with fixed look ahead */
/* package */ class ClothoidFixedControl extends LookAheadControl {
  private final CurveIntersection curveIntersection;

  public ClothoidFixedControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(lookAhead, maxTurningRate);
    curveIntersection = new PseudoSe2CurveIntersection(lookAhead);
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get().getU();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = //
        new Se2GroupElement(state).inverse()::combine;
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      // FIXME GJOEL what is the trick for reverse driving? can use Se2Letter6Demo for testing
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    Optional<Tensor> optional = curveIntersection.string(beacons);
    if (optional.isPresent()) {
      ClothoidPursuit clothoidPursuit = new ClothoidPursuit(optional.get());
      if (clothoidPursuit.firstRatio().isPresent()) {
        Scalar ratio = clothoidPursuit.firstRatio().get();
        if (clip.isInside(ratio)) {
          targetLocal = optional.get();
          return Optional.of(CarHelper.singleton(speed, ratio).getU());
        }
      }
    }
    targetLocal = null;
    return Optional.empty();
  }
}
