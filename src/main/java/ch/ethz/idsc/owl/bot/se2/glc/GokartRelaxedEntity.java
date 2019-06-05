// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;

public class GokartRelaxedEntity extends GokartEntity {
  public static GokartRelaxedEntity createRelaxedGokartEntity(StateTime stateTime, Tensor slacks) {
    return new GokartRelaxedEntity(stateTime, slacks);
  }

  // ---
  private final Tensor slacks;

  private GokartRelaxedEntity(StateTime stateTime, Tensor slacks) {
    super(stateTime);
    this.slacks = slacks;
  }

  @Override
  public final RelaxedTrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    // define goal region
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    // define region costs
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 3, 10 }, { 3, 0 }, { 10, 0 }, { 10, 15 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(regionConstraint, RealScalar.ONE);
    // define Se2MinTimeGoalManager
    Se2MinTimeGoalManager timeCosts = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    // cost vector
    List<CostFunction> costVector = Arrays.asList(timeCosts, regionCost);
    GoalInterface goalInterface = new VectorCostGoalAdapter(costVector, se2ComboRegion);
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface, slacks);
  }

  @Override
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new ConeRegion(goal, Degree.of(18));
  }

  public Tensor getSlack() {
    return this.slacks;
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // ANDRE FIXME
    // System.out.println(trajectoryPlanner.getQueue());
    getEdgeRender().setCollection(trajectoryPlanner.getQueue());
  }

  public enum Objectives {
    TIME_COSTS, REGION_COSTS, CORNER_CUTTING_COSTS,
  }

  // TODO ASTOLL function is not used (yet?)
  private List<CostFunction> createCostFunctionList(List<Objectives> objectives, Tensor goal) {
    List<CostFunction> costFunction = new ArrayList<>();
    Iterator<Objectives> iterator = objectives.iterator();
    while (iterator.hasNext()) {
      switch (iterator.next()) {
      case TIME_COSTS:
        goalRegion = getGoalRegionWithDistance(goal);
        Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
        Se2MinTimeGoalManager timeCosts = new Se2MinTimeGoalManager(se2ComboRegion, controls);
      case REGION_COSTS:
        Tensor polygon = Tensors.matrixFloat(new float[][] { { 3, 10 }, { 3, 0 }, { 10, 0 }, { 10, 15 } });
        PolygonRegion polygonRegion = new PolygonRegion(polygon);
        PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
        CostFunction regionCost = ConstraintViolationCost.of(regionConstraint, RealScalar.ONE);
      }
    }
    return costFunction;
  }
}
