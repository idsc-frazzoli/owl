// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;

/** pure pursuit */
// TODO rename class to reflect
public class CarTrajectoryControl extends StateTrajectoryControl {
  /** (vx, vy, omega) */
  // private static final Tensor FALLBACK_CONTROL = ;
  private static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));
  // ---
  private final Clip CLIP_TURNING_RATE = Clip.function(Degree.of(-50), Degree.of(+50));
  private final Scalar LOOKAHEAD = RealScalar.of(0.5);
  private final Scalar SPEED = RealScalar.of(1.0);

  // public CarTrajectoryControl() {
  // super(Array.zeros(3));
  // }
  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return SE2WRAP.distance(x, y);
  }

  // TODO for visualization
  private PurePursuit purePursuit = null;

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    // TODO controller is not able to execute backwards motion
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensor -> tensor.extract(0, 2)) //
        .map(tensorUnaryOperator));
    PurePursuit _purePursuit = PurePursuit.fromTrajectory(beacons, LOOKAHEAD);
    if (_purePursuit.ratio().isPresent()) {
      Scalar ratio = _purePursuit.ratio().get();
      if (CLIP_TURNING_RATE.isInside(ratio)) {
        purePursuit = _purePursuit;
        return Optional.of(CarFlows.singleton(SPEED, ratio).getU());
      }
    }
    purePursuit = null;
    return Optional.empty();
  }
}
