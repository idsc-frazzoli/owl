// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuit;
import ch.ethz.idsc.owl.math.pursuit.CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.pursuit.PursuitInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** clothoid pursuit control with fixed look ahead */
/* package */ class ClothoidFixedControl extends LookAheadControl {
  // ---
  private final CurveIntersection curveIntersection;

  public ClothoidFixedControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(lookAhead, maxTurningRate);
    curveIntersection = new PseudoSe2CurveIntersection(lookAhead);
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = //
        new Se2GroupElement(state).inverse()::combine;
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      ClothoidControlHelper.mirrorAndReverse(beacons);
    Optional<Tensor> optional = curveIntersection.string(beacons);
    if (optional.isPresent()) {
      PursuitInterface pursuitInterface = ClothoidPursuit.of(optional.get());
      if (pursuitInterface.firstRatio().isPresent()) {
        Scalar ratio = pursuitInterface.firstRatio().get();
        if (clip.isInside(ratio)) {
          Tensor targetLocal_ = Tensors.of(optional.get());
          if (Sign.isNegative(speed))
            ClothoidControlHelper.mirrorAndReverse(targetLocal_);
          targetLocal = targetLocal_.get(0);
          return Optional.of(Se2CarFlows.singleton(speed, ratio));
        }
      }
    }
    targetLocal = null;
    return Optional.empty();
  }
}
