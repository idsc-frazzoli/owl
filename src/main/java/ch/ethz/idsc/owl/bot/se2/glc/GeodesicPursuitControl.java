// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.ArgMinVariable;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuit;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuitInterface;
import ch.ethz.idsc.owl.math.planar.TrajectoryEntryFinder;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class GeodesicPursuitControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  private final static int MAX_LEVEL = 25;
  // ---
  private final TrajectoryEntryFinder entryFinder;
  private final List<DynamicRatioLimit> ratioClippers = new ArrayList<>();
  // ---
  private Tensor curve; // for visualization

  /** @param entryFinder strategy
   * @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public GeodesicPursuitControl(TrajectoryEntryFinder entryFinder, Scalar maxTurningRate) {
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
    Predicate<Scalar> isCompliant = isCompliant(state, speed);
    TensorScalarFunction mapping = vector -> { //
      GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(vector);
      Tensor ratios = geodesicPursuit.ratios();
      if (ratios.stream().map(Tensor::Get).allMatch(isCompliant))
        return curveLength(geodesicPursuit.curve()); // Norm._2.ofVector(Extract2D.FUNCTION.apply(vector));
      return DoubleScalar.POSITIVE_INFINITY;
    };
    Scalar var = ArgMinVariable.using(entryFinder, mapping, MAX_LEVEL).apply(beacons);
    Optional<Tensor> lookAhead = entryFinder.on(beacons).apply(var).point;
    if (lookAhead.isPresent()) {
      GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(lookAhead.get());
      curve = geodesicPursuit.curve();
      if (inReverse)
        mirrorAndReverse(curve);
      return Optional.of(CarHelper.singleton(speed, geodesicPursuit.firstRatio().get()).getU());
    }
    curve = null;
    // System.err.println("no compliant strategy found!");
    return Optional.empty();
  }

  /** mirror the points along the y axis and invert their orientation
   * @param se2points curve given by points {x,y,a} */
  private static void mirrorAndReverse(Tensor se2points) {
    se2points.set(Scalar::negate, Tensor.ALL, 0);
    se2points.set(Scalar::negate, Tensor.ALL, 2);
  }

  /** @param state
   * @param speed
   * @return predicate to determine whether ratio is compliant with all posed turning ratio limits */
  private Predicate<Scalar> isCompliant(Tensor state, Scalar speed) {
    return ratio -> ratioClippers.stream().map(c -> c.at(state, speed)).allMatch(c -> c.isInside(ratio));
  }

  /** @param curve geodesic
   * @return approximated length of curve */
  private static Scalar curveLength(Tensor curve) {
    Tensor curve_ = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    int n = curve_.length();
    return curve_.extract(1, n).subtract(curve_.extract(0, n - 1)).stream().map(Norm._2::ofVector).reduce(Scalar::add).get();
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
