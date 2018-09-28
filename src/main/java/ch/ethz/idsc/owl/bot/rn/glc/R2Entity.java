// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.bot.rn.RnMinTimeGoalManager;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.MultiCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.AbstractCircularEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** omni-directional movement with constant speed
 * 
 * the implementation chooses certain values */
/* package */ class R2Entity extends AbstractCircularEntity {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(8, 8).unmodifiable();
  public static final FixedStateIntegrator FIXEDSTATEINTEGRATOR = //
      FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 12), 4);
  // ---
  /** extra cost functions, for instance to prevent cutting corners */
  public final Collection<CostFunction> extraCosts = new LinkedList<>();
  protected final R2Flows r2Flows = new R2Flows(RealScalar.ONE);
  protected RegionWithDistance<Tensor> goalRegion = null;

  /** @param state initial position of entity */
  public R2Entity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    // add(new FallbackControl(Array.zeros(2)));
  }

  @Override
  public Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    /** preserve 0.5[s] of the former trajectory
     * planning should not exceed that duration, otherwise
     * the entity may not be able to follow a planned trajectory */
    return RealScalar.of(0.5);
  }

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    Tensor partitionScale = PARTITION_SCALE;
    Scalar goalRadius = RealScalar.of(Math.sqrt(2.0)).divide(partitionScale.Get(0));
    return new SphericalRegion(goal.extract(0, 2), goalRadius);
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Collection<Flow> controls = createControls(); // TODO design no good
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        RnMinTimeGoalManager.create(goalRegion, controls), //
        extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, //
        plannerConstraint, goalInterface);
  }

  Collection<Flow> createControls() {
    /** 36 corresponds to 10[Degree] resolution */
    return r2Flows.getFlows(36);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITION_SCALE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
  }
}
