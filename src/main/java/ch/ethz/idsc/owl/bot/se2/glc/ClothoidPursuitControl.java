// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.pursuit.ArgMinVariable;
import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuit;
import ch.ethz.idsc.owl.math.pursuit.GeodesicPursuitInterface;
import ch.ethz.idsc.owl.math.pursuit.TrajectoryEntryFinder;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class ClothoidPursuitControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  private final static int MAX_LEVEL = 20;
  private final static int REFINEMENT = 2;
  // ---
  private final TrajectoryEntryFinder entryFinder;
  private final List<DynamicRatioLimit> ratioClippers = new ArrayList<>();
  // ---
  private Tensor curve; // for visualization

  /** @param entryFinder strategy
   * @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public ClothoidPursuitControl(TrajectoryEntryFinder entryFinder, Scalar maxTurningRate) {
    this.entryFinder = entryFinder;
    addRatioLimit(new StaticRatioLimit(maxTurningRate));
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Norm2Squared.ofVector(Se2Wrap.INSTANCE.difference(x, y));
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Scalar speed = trailAhead.get(0).getFlow().get().getU().Get(0);
    boolean inReverse = Sign.isNegative(speed);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(state).inverse()::combine;
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (inReverse)
      mirrorAndReverse(beacons);
    // ---
    TensorScalarFunction costMapping = new ClothoidLengthCostFunction(isCompliant(state, speed), REFINEMENT);
    Scalar var = ArgMinVariable.using(entryFinder, costMapping, MAX_LEVEL).apply(beacons);
    Optional<Tensor> lookAhead = entryFinder.on(beacons).apply(var).point();
    if (lookAhead.isPresent()) {
      Tensor xya = lookAhead.get();
      GeodesicPursuitInterface geodesicPursuitInterface = new ClothoidPursuit(xya);
      curve = ClothoidPursuit.curve(xya, REFINEMENT);
      if (inReverse)
        mirrorAndReverse(curve);
      return Optional.of(CarHelper.singleton(speed, geodesicPursuitInterface.firstRatio().get()).getU());
    }
    curve = null;
    // System.err.println("no compliant strategy found!");
    return Optional.empty();
  }

  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x, y, a} */
  private static void mirrorAndReverse(Tensor se2points) {
    se2points.set(Scalar::negate, Tensor.ALL, 0);
    se2points.set(Scalar::negate, Tensor.ALL, 2);
  }

  /** @param state
   * @param speed
   * @return predicate to determine whether ratio is compliant with all posed turning ratio limits */
  private Predicate<Scalar> isCompliant(Tensor state, Scalar speed) {
    List<Clip> list = ratioClippers.stream() //
        .map(dynamicRatioLimit -> dynamicRatioLimit.at(state, speed)) //
        .collect(Collectors.toList());
    return ratio -> list.stream().allMatch(clip -> clip.isInside(ratio));
  }

  /** @param dynamicLimit on turning ratio depending on state and speed */
  public void addRatioLimit(DynamicRatioLimit dynamicLimit) {
    ratioClippers.add(dynamicLimit);
  }

  @Override // fromTrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    return Optional.ofNullable(curve).map(geometricLayer::toPath2D);
  }
}
