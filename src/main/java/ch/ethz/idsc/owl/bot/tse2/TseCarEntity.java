// code by ynager, jph
package ch.ethz.idsc.owl.bot.tse2;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class TseCarEntity extends Tse2Entity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(7).reciprocal(), RealScalar.of(10)).unmodifiable();
  static final Scalar MAX_SPEED = RealScalar.of(2.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(45);
  static final Scalar LOOKAHEAD = RealScalar.of(0.5);
  static final Scalar MAX_TURNING_RATE = Degree.of(50); // slightly higher for pure pursuit
  static final FlowsInterface CARFLOWS = Tse2CarFlows.of(Tensors.vector(-0.7, 0, 0.7), MAX_TURNING_PLAN);
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
  static final Tse2Wrap TSE2WRAP = new Tse2Wrap(Tensors.vector(1, 1, 2, 2));

  public static TseCarEntity createDefault(StateTime stateTime) {
    return new TseCarEntity(stateTime, //
        new PurePursuitControlTse2(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITIONSCALE, CARFLOWS, SHAPE);
  }

  // ---
  protected final Collection<Flow> controls;
  public final Tensor goalRadius;
  final Tensor partitionScale;
  private final Tensor shape;
  protected final TrajectoryControl trajectoryControl; // TODO design is despicable

  /** extra cost functions, for instance
   * @param stateTime initial position */
  public TseCarEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, Tensor shape) {
    super(stateTime, trajectoryControl);
    this.trajectoryControl = trajectoryControl;
    this.controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    final Scalar goalRadius_v = SQRT2.divide(PARTITIONSCALE.Get(3));
    this.goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta, goalRadius_v).unmodifiable();
    this.partitionScale = partitionScale.unmodifiable();
    this.shape = shape.copy().unmodifiable();
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.5);
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return TSE2WRAP.distance(x, y); // non-negative
  }

  protected RegionWithDistance<Tensor> goalRegion = null;

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goal = goal.append(RealScalar.ZERO); // 4th component of goal
    // TODO add max velocity constraint
    Tse2ComboRegion se2ComboRegion = Tse2ComboRegion.spherical(goal, goalRadius);
    Tse2MinTimeGoalManager se2MinTimeGoalManager = new Tse2MinTimeGoalManager(se2ComboRegion, controls, MAX_SPEED);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(se2MinTimeGoalManager.getGoalInterface(), extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  @Override // from Se2Entity
  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(partitionScale, StateTimeTensorFunction.state(TSE2WRAP::represent));
  }

  @Override // from Se2Entity
  protected Tensor shape() {
    return shape;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
    // ---
  }

  public Tensor coords_X() {
    ScalarSummaryStatistics scalarSummaryStatistics = //
        shape.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    return Subdivide.of(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax(), 2);
  }
}
