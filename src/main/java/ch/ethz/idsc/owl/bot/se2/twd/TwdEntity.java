// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.Se2Entity;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class TwdEntity extends Se2Entity {
  private static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(6), RealScalar.of(6), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  // triangle
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).unmodifiable();
  private static final Se2Wrap SE2WRAP = new Se2Wrap(Tensors.vectorDouble(1, 1, 2));

  public static TwdEntity createDuckie(StateTime stateTime) {
    TwdEntity twdEntity = new TwdEntity( //
        stateTime, new TwdTrajectoryControl(), new TwdDuckieFlows(RealScalar.ONE, RealScalar.ONE));
    twdEntity.extraCosts.add(Se2LateralAcceleration.INSTANCE);
    return twdEntity;
  }

  public static TwdEntity createJ2B2(StateTime stateTime) {
    return new TwdEntity( //
        stateTime, new TwdTrajectoryControl(), new TwdForwardFlows(RealScalar.ONE, RealScalar.ONE));
  }

  // ---
  final Collection<Flow> controls;
  final Scalar goalRadius_xy;
  final Scalar goalRadius_theta;

  /** @param twdConfig
   * @param stateTime initial position */
  protected TwdEntity(StateTime stateTime, TrajectoryControl trajectoryControl, TwdFlows twdConfig) {
    super(stateTime, trajectoryControl);
    controls = twdConfig.getFlows(4);
    Tensor eta = eta();
    goalRadius_xy = SQRT2.divide(eta.Get(0));
    goalRadius_theta = SQRT2.divide(eta.Get(2));
  }

  @Override
  public Scalar distance(Tensor x, Tensor y) {
    return SE2WRAP.distance(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.ONE;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    this.plannerConstraint = plannerConstraint;
    Tensor radiusVector = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.spherical(goal, radiusVector);
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        se2MinTimeGoalManager.getGoalInterface(), //
        extraCosts);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.represent = StateTimeTensorFunction.state(SE2WRAP::represent);
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    return PARTITIONSCALE;
  }

  @Override
  protected Tensor shape() {
    return SHAPE;
  }
}
