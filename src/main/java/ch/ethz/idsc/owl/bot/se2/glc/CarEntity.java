// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2ShiftCostFunction;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.ani.PlannerType;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class CarEntity extends Se2Entity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(1.0);
  static final Scalar LOOKAHEAD = RealScalar.of(0.5);
  /** the pure pursuit controller is permitted a slightly higher turning rate "rad/m"
   * than the planner, to overcome small imprecisions when following the trajectory */
  static final Clip CLIP_TURNING_RATE = Clip.function(Degree.of(-50), Degree.of(+50));
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
    return new CarEntity(stateTime, new CarTrajectoryControl(), PARTITIONSCALE, CARFLOWS, SHAPE);
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
  public CarEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, CarFlows carFlows, Tensor shape) {
    super(stateTime, trajectoryControl);
    // new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime));
    controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITIONSCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITIONSCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta);
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
    this.partitionScale = partitionScale;
    this.shape = shape.copy().unmodifiable();
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
    if (!VectorQ.ofLength(goal, 3))
      throw TensorRuntimeException.of(goal);
    this.obstacleQuery = obstacleQuery;
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        Se2MinTimeGoalManager.create(goal, goalRadius, controls), extraCosts);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta(), FIXEDSTATEINTEGRATOR, controls, new TrajectoryObstacleConstraint(obstacleQuery), goalInterface);
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

  private PurePursuit purePursuit = null;

  // @Override // from AbstractEntity
  // protected Optional<Tensor> customControl(List<TrajectorySample> trailAhead) {
  // // TODO controller is not able to execute backwards motion
  // Tensor state = getStateTimeNow().state();
  // TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
  // Tensor beacons = Tensor.of(trailAhead.stream() //
  // .map(TrajectorySample::stateTime) //
  // .map(StateTime::state) //
  // .map(tensor -> tensor.extract(0, 2)) //
  // .map(tensorUnaryOperator));
  // PurePursuit _purePursuit = PurePursuit.fromTrajectory(beacons, LOOKAHEAD);
  // if (_purePursuit.ratio().isPresent()) {
  // Scalar ratio = _purePursuit.ratio().get();
  // if (CLIP_TURNING_RATE.isInside(ratio)) {
  // purePursuit = _purePursuit;
  // return Optional.of(CarFlows.singleton(SPEED, ratio).getU());
  // }
  // }
  // purePursuit = null;
  // return Optional.empty();
  // }
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    PurePursuit _purePursuit = purePursuit;
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
