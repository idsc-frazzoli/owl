// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.PlannerType;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class CarEntity extends Se2Entity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.ONE;
  static final CarFlows CARFLOWS = new CarStandardFlows(SPEED, Degree.of(45));
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SHIFT_PENALTY = RealScalar.of(0.4);
  // ---
  static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { //
          { .2, +.07 }, //
          { .25, +.0 }, //
          { .2, -.07 }, //
          { -.1, -.07 }, //
          { -.1, +.07 } //
      }).unmodifiable();
  // ---
  static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vector(1, 1, 2));

  public static CarEntity createDefault(StateTime stateTime) {
    return new CarEntity(stateTime, PARTITIONSCALE, CARFLOWS, SHAPE);
  }

  // ---
  private final Collection<Flow> controls;
  private final Tensor goalRadius;
  private final Tensor partitionScale;
  private final Tensor shape;

  /** extra cost functions, for instance
   * 1) to penalize switching gears
   * 2) to prevent cutting corners
   * 
   * @param stateTime initial position */
  public CarEntity(StateTime stateTime, Tensor partitionScale, CarFlows carFlows, Tensor shape) {
    super(new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime));
    controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
    this.partitionScale = partitionScale;
    this.shape = shape.copy().unmodifiable();
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return SE2WRAP.distance(x, y);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.5);
  }

  @Override
  public PlannerType getPlannerType() {
    return PlannerType.STANDARD;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    GlobalAssert.that(VectorQ.ofLength(goal, 3));
    this.obstacleQuery = obstacleQuery;
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        Se2MinTimeGoalManager.create(goal, goalRadius, controls), extraCosts);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta(), FIXEDSTATEINTEGRATOR, controls, obstacleQuery, goalInterface);
    trajectoryPlanner.represent = StateTimeTensorFunction.state(SE2WRAP::represent);
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    return partitionScale;
  }

  @Override
  protected Tensor shape() {
    return shape;
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(List<TrajectorySample> trailAhead) {
    Tensor state = getStateTimeNow().state();
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(t -> t.extract(0, 2)) //
        .map(tensorUnaryOperator));
    Optional<Tensor> optional = interpolate(beacons, RealScalar.of(.5)); // TODO magic const
    if (optional.isPresent()) { //
      Tensor lookAhead = optional.get(); // {x, y}
      Scalar angle = ArcTan.of(lookAhead.Get(0), lookAhead.Get(1));
      Flow flow = CarFlows.singleton(SPEED, angle.multiply(RealScalar.of(2)));
      return Optional.of(flow.getU());
    }
    System.err.println("flow fail");
    return Optional.empty();
  }

  public static Optional<Tensor> interpolate(Tensor beacons, Scalar distance) {
    for (int count = 1; count < beacons.length(); ++count) {
      Tensor prev = beacons.get(count - 1);
      Tensor next = beacons.get(count - 0);
      Scalar lo = Norm._2.of(prev);
      Scalar hi = Norm._2.of(next);
      if (Scalars.lessEquals(lo, distance) && Scalars.lessEquals(distance, hi)) {
        Clip clip = Clip.function(lo, hi);
        if (clip.isInside(distance)) {
          Scalar lambda = clip.rescale(distance);
          Interpolation interpolation = LinearInterpolation.of(Tensors.of(prev, next));
          return Optional.of(interpolation.get(Tensors.of(lambda)));
        }
      }
    }
    return Optional.empty();
  }
}
