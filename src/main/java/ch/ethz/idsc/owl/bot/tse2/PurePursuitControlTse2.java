// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
public class PurePursuitControlTse2 extends StateTrajectoryControl {
  private final Scalar lookAhead;
  private final Clip clip;

  public PurePursuitControlTse2(Scalar lookAhead, Scalar maxTurningRate) {
    this.lookAhead = lookAhead;
    this.clip = Clip.function(maxTurningRate.negate(), maxTurningRate);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return Norm._2.ofVector(Tse2Wrap.INSTANCE.difference(x, y));
  }

  PurePursuit purePursuit = null;

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get().getU();
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
    if (_purePursuit.ratio().isPresent()) {
      Scalar ratio = _purePursuit.ratio().get();
      if (clip.isInside(ratio)) {
        purePursuit = _purePursuit;
        return Optional.of(Tse2CarHelper.singleton(speed, ratio).getU());
      }
    }
    purePursuit = null;
    return Optional.empty();
  }
}
