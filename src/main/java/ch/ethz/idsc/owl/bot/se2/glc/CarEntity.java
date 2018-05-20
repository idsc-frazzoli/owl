// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class CarEntity extends Se2Entity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(1.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(45);
  static final Scalar LOOKAHEAD = RealScalar.of(0.5);
  /** the pure pursuit controller is permitted a slightly higher turning rate "rad/m"
   * than the planner, to overcome small imprecisions when following the trajectory */
  static final Scalar MAX_TURNING_RATE = Degree.of(50);
  static final FlowsInterface CARFLOWS = CarFlows.standard(SPEED, MAX_TURNING_PLAN);
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
    return new CarEntity(stateTime, //
        new PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITIONSCALE, CARFLOWS, SHAPE);
  }

  // ---
  private final Collection<Flow> controls;
  public final Tensor goalRadius;
  private final Tensor partitionScale;
  private final Tensor shape;
  protected final TrajectoryControl trajectoryControl; // TODO design is despicable

  /** extra cost functions, for instance
   * 1) to penalize switching gears
   * 2) to prevent cutting corners
   * 
   * @param stateTime initial position */
  public CarEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, Tensor shape) {
    super(stateTime, trajectoryControl);
    this.trajectoryControl = trajectoryControl;
    // new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime));
    controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta).unmodifiable();
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
    this.partitionScale = partitionScale;
    this.shape = shape.copy().unmodifiable();
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.5);
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return SE2WRAP.distance(x, y); // non-negative
  }

  private RegionWithDistance<Tensor> goalRegion = null;

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new SphericalRegion(goal.extract(0, 2), goalRadius.Get(0));
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    this.plannerConstraint = plannerConstraint;
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, new So2Region(goal.Get(2), goalRadius.Get(2)));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(se2MinTimeGoalManager.getGoalInterface(), extraCosts);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
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

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
    // ---
    if (trajectoryControl instanceof PurePursuitControl) {
      PurePursuitControl purePursuitControl = (PurePursuitControl) trajectoryControl;
      PurePursuit _purePursuit = purePursuitControl.purePursuit;
      if (Objects.nonNull(_purePursuit) && _purePursuit.lookAhead().isPresent()) {
        StateTime stateTime = getStateTimeNow();
        Tensor matrix = Se2Utils.toSE2Matrix(stateTime.state());
        geometricLayer.pushMatrix(matrix);
        graphics.setColor(Color.RED);
        graphics.draw(geometricLayer.toVector(Array.zeros(2), _purePursuit.lookAhead().get()));
        geometricLayer.popMatrix();
      }
    }
  }

  public Tensor coords_X() {
    ScalarSummaryStatistics scalarSummaryStatistics = //
        shape.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    return Subdivide.of(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax(), 2);
  }
}
