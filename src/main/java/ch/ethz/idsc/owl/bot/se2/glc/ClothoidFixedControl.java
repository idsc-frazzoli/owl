// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.ClothoidPursuit;
import ch.ethz.idsc.owl.math.planar.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
/* package */ class ClothoidFixedControl extends Se2TrajectoryControl {
  private final Scalar lookAhead;
  /** for drawing only */
  private Tensor targetLocal = null;

  public ClothoidFixedControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(Clips.interval(maxTurningRate.negate(), maxTurningRate));
    this.lookAhead = lookAhead;
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get().getU();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(state).inverse()::combine;
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    Optional<Tensor> optional = new PseudoSe2CurveIntersection(lookAhead).string(beacons);
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

  @Override // from TrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    Tensor _targetLocal = targetLocal; // copy reference
    if (Objects.nonNull(_targetLocal))
      return Optional.of(geometricLayer.toLine2D(Array.zeros(2), _targetLocal));
    return Optional.empty();
  }
}
