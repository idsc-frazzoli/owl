// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2LateralAcceleration;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.Se2Entity;
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
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class TwdEntity extends Se2Entity {
  static final Tensor PARTITION_SCALE = Tensors.of( //
      RealScalar.of(6), RealScalar.of(6), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  static final Tensor SHAPE = Arrowhead.of(0.3);

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
  final Collection<Tensor> controls;
  final Scalar goalRadius_xy;
  final Scalar goalRadius_theta;

  /** @param twdConfig
   * @param stateTime initial position */
  protected TwdEntity(StateTime stateTime, TrajectoryControl trajectoryControl, TwdFlows twdConfig) {
    super(stateTime, trajectoryControl);
    controls = twdConfig.getFlows(4);
    Tensor eta = PARTITION_SCALE;
    goalRadius_xy = SQRT2.divide(eta.Get(0));
    goalRadius_theta = SQRT2.divide(eta.Get(2));
  }

  @Override // from TensorMetric
  public Scalar distance(Tensor x, Tensor y) {
    return Vector2Norm.of(Se2Wrap.INSTANCE.difference(x, y)); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.ONE;
  }

  private RegionWithDistance<Tensor> goalRegion = null;

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new BallRegion(Extract2D.FUNCTION.apply(goal), goalRadius_xy);
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = //
        new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius_theta));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        se2MinTimeGoalManager.getGoalInterface(), //
        extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITION_SCALE, StateTimeTensorFunction.state(Se2Wrap.INSTANCE::represent));
  }

  @Override
  protected Tensor shape() {
    return SHAPE;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
  }
}
