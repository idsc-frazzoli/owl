// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractCircularEntity;
import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;

/* package */ class LvEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  private static final Tensor PARTITION_SCALE = Tensors.vector(8, 8).unmodifiable();
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  // ---
  private final FixedStateIntegrator fixedStateIntegrator;
  private final TreeRender treeRender = new TreeRender();
  private final Collection<Tensor> controls;

  /** @param state initial position of entity */
  public LvEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, StateSpaceModel stateSpaceModel, Collection<Tensor> controls) {
    super(episodeIntegrator, trajectoryControl);
    add(FallbackControl.of(Array.zeros(1)));
    fixedStateIntegrator = //
        FixedStateIntegrator.create(INTEGRATOR, stateSpaceModel, RationalScalar.of(1, 12), 4);
    this.controls = controls;
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.ONE;
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    GoalInterface goalInterface = LvGoalInterface.create(Extract2D.FUNCTION.apply(goal), Tensors.vector(0.2, 0.2));
    StateTimeRaster stateTimeRaster = EtaRaster.state(PARTITION_SCALE);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, fixedStateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    treeRender.render(geometricLayer, graphics);
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
