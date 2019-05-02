// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.ClothoidPursuit;
import ch.ethz.idsc.owl.math.planar.PseudoSe2CurveIntersection;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** pure pursuit control */
/* package */ class ClothoidFixedControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  private final Clip clip;
  private final Scalar lookAhead;
  private ClothoidPursuit purePursuit = null;

  public ClothoidFixedControl(Scalar lookAhead, Scalar maxTurningRate) {
    this.lookAhead = lookAhead;
    this.clip = Clips.interval(maxTurningRate.negate(), maxTurningRate);
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Norm2Squared.ofVector(Se2Wrap.INSTANCE.difference(x, y));
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
      ClothoidPursuit _clothoidPursuit = new ClothoidPursuit(optional.get());
      // PurePursuit _purePursuit = new PurePursuit(new SphereCurveIntersection(lookAhead).string(beacons));
      // PurePursuit.fromTrajectory(, );
      if (_clothoidPursuit.firstRatio().isPresent()) {
        Scalar ratio = _clothoidPursuit.firstRatio().get();
        if (clip.isInside(ratio)) {
          purePursuit = _clothoidPursuit;
          return Optional.of(CarHelper.singleton(speed, ratio).getU());
        }
      }
    }
    purePursuit = null;
    return Optional.empty();
  }

  @Override // from TrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    // TODO
    // ClothoidPursuit _purePursuit = purePursuit; // copy reference
    // if (Objects.nonNull(_purePursuit) && _purePursuit.lookAhead().isPresent())
    // return Optional.of(geometricLayer.toVector(Array.zeros(2), _purePursuit.lookAhead().get()));
    return Optional.empty();
  }
}
