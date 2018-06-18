// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
public class PurePursuitControl extends StateTrajectoryControl {
  /** (vx, vy, omega) */
  private static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final Clip clip;
  private final Scalar lookAhead;

  public PurePursuitControl(Scalar lookAhead, Scalar maxTurningRate) {
    this.lookAhead = lookAhead;
    this.clip = Clip.function(maxTurningRate.negate(), maxTurningRate);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return SE2WRAP.distance(x, y);
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
        .map(tensor -> tensor.extract(0, 2)) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    PurePursuit _purePursuit = PurePursuit.fromTrajectory(beacons, lookAhead);
    if (_purePursuit.ratio().isPresent()) {
      Scalar ratio = _purePursuit.ratio().get();
      if (clip.isInside(ratio)) {
        purePursuit = _purePursuit;
        return Optional.of(CarHelper.singleton(speed, ratio).getU());
      }
    }
    purePursuit = null;
    return Optional.empty();
  }
}
