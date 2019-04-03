// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.GeodesicPursuit;
import ch.ethz.idsc.owl.math.planar.TrajectoryEntryFinder;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Increment;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class GeodesicPursuitControl extends StateTrajectoryControl {
  private final static GeodesicInterface GEODESIC = ClothoidCurve.INSTANCE;
  // ---
  private final TrajectoryEntryFinder entryFinder;
  private final Clip staticClip; // turning ratios limits
  private Optional<Clip> dynamicClip = Optional.empty(); // state dependent turning ratios limits

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
    Tensor state = tail.state();
    dynamicClip = dynamicClip(state, speed);
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
//        .map(tensorUnaryOperator)); // TODO change {x, y} -> {x, y, a}
        .map(t -> tensorUnaryOperator.apply(t).append(t.Get(2).subtract(state.Get(2))))); // TODO could be part of Se2Bijection
    if (Sign.isNegative(speed))
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    // ---
    // TODO proper rejection/optimization e.g. with bisection
    Optional<Tensor> lookAhead = entryFinder.initial(beacons);
    Function<Scalar, Optional<Tensor>> function = entryFinder.on(beacons);
    for (int i = 0; i < beacons.length(); i++) {
      GeodesicPursuit geodesicPursuit = new GeodesicPursuit(GEODESIC, lookAhead);
      Optional<Tensor> ratios = geodesicPursuit.ratios();
      if (ratios.isPresent() && ratios.get().stream().allMatch(t -> isCompliant(t.Get())))
        return Optional.of(CarHelper.singleton(speed, geodesicPursuit.ratio().get()).getU());
      Scalar next = Increment.ONE.apply(entryFinder.currentVar());
      lookAhead = function.apply(next);
    }
    System.err.println("no compliant strategy found!");
    return Optional.empty();
  }

  private boolean isCompliant(Scalar ratio) {
    return staticClip.isInside(ratio) && (!dynamicClip.isPresent() || dynamicClip.get().isInside(ratio));
  }

  private Optional<Clip> dynamicClip(Tensor state, Scalar speed) {
    // TODO implement this
    return Optional.empty();
  }

}
