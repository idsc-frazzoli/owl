// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.pursuit.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
/* package */ class PurePursuitControl extends Se2TrajectoryControl {
  private final Scalar lookAhead;
  private PurePursuit purePursuit = null;

  /** @param lookAhead distance
   * @param maxTurningRate */
  public PurePursuitControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(Clips.absolute(maxTurningRate));
    this.lookAhead = lookAhead;
  }

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
        return Optional.of(CarHelper.singleton(speed, ratio).getU());
      }
    }
    purePursuit = null;
    return Optional.empty();
  }

  @Override // from TrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    PurePursuit _purePursuit = purePursuit; // copy reference
    if (Objects.nonNull(_purePursuit) && _purePursuit.lookAhead().isPresent())
      return Optional.of(geometricLayer.toLine2D(Array.zeros(2), _purePursuit.lookAhead().get()));
    return Optional.empty();
  }
}
