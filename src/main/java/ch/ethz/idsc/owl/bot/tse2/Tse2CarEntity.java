// code by ynager, jph
package ch.ethz.idsc.owl.bot.tse2;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class Tse2CarEntity extends Tse2Entity {
  static final Tensor PARTITION_SCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(7).reciprocal(), RealScalar.of(10)).unmodifiable();
  public static final Scalar MAX_SPEED = RealScalar.of(3.0); //
  public static final Clip v_range = Clips.positive(MAX_SPEED);
  static final Scalar MAX_TURNING_PLAN = Degree.of(45);
  static final Scalar LOOKAHEAD = RealScalar.of(0.5);
  static final Scalar MAX_TURNING_RATE = Degree.of(50); // slightly higher for pure pursuit
  static final FlowsInterface CARFLOWS = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-0.7, 0, 0.7));
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
  protected Scalar goalVelocity = RealScalar.ZERO;
  // ---

  public static Tse2CarEntity createDefault(StateTime stateTime) {
    return new Tse2CarEntity(stateTime, //
        new Tse2PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITION_SCALE, CARFLOWS, SHAPE);
  }

  // ---
  protected final Collection<Tensor> controls;
  protected final Tensor goalRadius;
  final Tensor partitionScale;
  private final Tensor shape;
  protected final TrajectoryControl trajectoryControl; // TODO JPH design is despicable

  /** extra cost functions, for instance
   * @param stateTime initial position */
  public Tse2CarEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, Tensor shape) {
    super(v_range, stateTime, trajectoryControl);
    this.trajectoryControl = trajectoryControl;
    this.controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITION_SCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITION_SCALE.Get(2));
    final Scalar goalRadius_v = SQRT2.divide(PARTITION_SCALE.Get(3));
    this.goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta, goalRadius_v);
    this.partitionScale = partitionScale.unmodifiable();
    this.shape = shape.copy().unmodifiable();
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
  }

  public void setVelGoal(Scalar vel, Scalar radius) {
    this.goalVelocity = vel;
    goalRadius.set(radius, 3);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.5);
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Tse2Wrap.INSTANCE.difference(x, y));
  }

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new BallRegion(Extract2D.FUNCTION.apply(goal), goalRadius.Get(0));
  }

  protected RegionWithDistance<Tensor> goalRegion = null;

  @Override // from TrajectoryEntity
  public TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goal = Append.of(goal, goalVelocity);
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(goal, goalRadius);
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, controls, MAX_SPEED);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(tse2MinTimeGoalManager.getGoalInterface(), extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), fixedStateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override // from Se2Entity
  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(partitionScale, StateTimeTensorFunction.state(Tse2Wrap.INSTANCE::represent));
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
    return Subdivide.increasing(scalarSummaryStatistics.getClip(), 2);
  }
}
