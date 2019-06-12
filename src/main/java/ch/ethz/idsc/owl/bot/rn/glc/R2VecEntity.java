// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.rn.RnMinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.EdgeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.order.DiscretizedLexicographic;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ class R2VecEntity extends R2Entity {
  public R2VecEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    System.out.println("goal=" + goal);
    Collection<Flow> controls = createControls(); // LONGTERM design no good
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface minTimeGoal = RnMinTimeGoalManager.create(goalRegion, controls); //
    List<CostFunction> costs = new ArrayList<>();
    getPrimaryCost().map(costs::add);
    costs.add(minTimeGoal);
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, goalRegion);
    Tensor slack = Array.zeros(costs.size()); // slack equal to zero for now
    Comparator<Tensor> comparator = DiscretizedLexicographic.of(slack);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, //
        plannerConstraint, goalInterface, new LexicographicRelabelDecision(comparator));
  }

  public Optional<CostFunction> getPrimaryCost() {
    return Optional.empty();
  }

  private final EdgeRender edgeRender = new EdgeRender();

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    edgeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
  }
}
