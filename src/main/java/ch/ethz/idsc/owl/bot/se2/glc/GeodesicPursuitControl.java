// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.ArgMinVariable;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuit;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuitInterface;
import ch.ethz.idsc.owl.math.planar.TrajectoryEntryFinder;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Increment;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class GeodesicPursuitControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  private final static GeodesicInterface GEODESIC = ClothoidCurve.INSTANCE;
  private final static int MAX_LEVEL = 25;
  // ---
  private final TrajectoryEntryFinder entryFinder;
  private final Clip staticClip; // fixed turning ratio limits
  private Optional<Clip> dynamicClip = Optional.empty(); // state and speed dependent turning ratio limits
  // ---
  private Tensor curve; // for visualization

  /** @param entryFinder strategy
   * @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public GeodesicPursuitControl(TrajectoryEntryFinder entryFinder, Scalar maxTurningRate) {
    this.entryFinder = entryFinder;
    staticClip = Clips.interval(maxTurningRate.negate(), maxTurningRate);
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
    dynamicClip = dynamicClip(state, speed);
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        // .map(tensorUnaryOperator)); // TODO change {x, y} -> {x, y, a}
        .map(t -> tensorUnaryOperator.apply(t).append(t.Get(2).subtract(state.Get(2))))); // TODO could be part of Se2Bijection
    if (inReverse)
      mirrorAndReverse(beacons);
    // ---
    Function<Tensor, Scalar> mapping = vector -> { //
      GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(GEODESIC, vector);
      Tensor ratios = geodesicPursuit.ratios();
      if (ratios.stream().map(Tensor::Get).allMatch(this::isCompliant))
        return Norm._2.ofVector(Extract2D.FUNCTION.apply(vector));
      return RealScalar.of(Double.MAX_VALUE);
    };
    Scalar var = ArgMinVariable.using(entryFinder, mapping, MAX_LEVEL).apply(beacons);
    Optional<Tensor> lookAhead = entryFinder.on(beacons).apply(var);
    if (lookAhead.isPresent()) {
      GeodesicPursuitInterface geodesicPursuit = new GeodesicPursuit(GEODESIC, lookAhead.get());
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

  /** @param ratio
   * @return whether ratio is compliant with current limits */
  private boolean isCompliant(Scalar ratio) {
    return staticClip.isInside(ratio) && (!dynamicClip.isPresent() || dynamicClip.get().isInside(ratio));
  }

  /** @param state of car
   * @param speed of car
   * @return dependent limit on turning ratio */
  private Optional<Clip> dynamicClip(Tensor state, Scalar speed) {
    // TODO implement this
    return Optional.empty();
  }

  @Override // fromTrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    return Optional.ofNullable(curve).map(geometricLayer::toPath2D);
  }
}
