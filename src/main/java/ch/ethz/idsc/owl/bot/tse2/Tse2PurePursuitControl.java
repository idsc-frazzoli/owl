// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.pursuit.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.hs.r2.Se2Bijection;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
/* package */ class Tse2PurePursuitControl extends StateTrajectoryControl {
  private final Scalar lookAhead;
  private final Clip clip;

  public Tse2PurePursuitControl(Scalar lookAhead, Scalar maxTurningRate) {
    this.lookAhead = lookAhead;
    this.clip = Clips.absolute(maxTurningRate);
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Tse2Wrap.INSTANCE.difference(x, y).extract(0, 3));
  }

  PurePursuit purePursuit = null;

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    PurePursuit _purePursuit = PurePursuit.fromTrajectory(beacons, lookAhead);
    if (_purePursuit.firstRatio().isPresent()) {
      Scalar ratio = _purePursuit.firstRatio().get();
      if (clip.isInside(ratio)) {
        purePursuit = _purePursuit;
        return Optional.of(Tse2CarHelper.singleton(speed, ratio));
      }
    }
    purePursuit = null;
    return Optional.empty();
  }
}
