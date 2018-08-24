package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import ch.ethz.idsc.owl.bot.tse2.Tse2CarEntity;
import ch.ethz.idsc.owl.bot.tse2.Tse2CarFlows;
import ch.ethz.idsc.owl.bot.tse2.Tse2ComboRegion;
import ch.ethz.idsc.owl.bot.tse2.Tse2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.LexicographicRelabelDecision;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CTrajectoryPlanner;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.CustomNodeMeritComparator;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.DiscretizedLexicographic;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;

public class Tse2GokartVecEntity extends Tse2CarEntity {
  static final Tensor PARTITIONSCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(20).reciprocal(), RealScalar.of(5)).unmodifiable();
  static final Scalar LOOKAHEAD = RealScalar.of(3.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(15);
  static final Scalar MAX_TURNING_RATE = Degree.of(23);
  public static final Tensor SHAPE = ResourceData.of("/gokart/footprint/20171201.csv");
  static final FlowsInterface CARFLOWS = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-0.7, 0, 0.7));
  //
  private List<CostFunction> costVector = new ArrayList<>();
  private List<Double> slackVector = new ArrayList<>();
  private Optional<Integer> timeCostPriority = Optional.empty();
  private Optional<Double> timeCostSlack = Optional.empty();

  public static Tse2GokartVecEntity createDefault(StateTime stateTime) {
    return new Tse2GokartVecEntity(stateTime, //
        new PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITIONSCALE, //
        CARFLOWS, //
        SHAPE);
  }

  public Tse2GokartVecEntity(StateTime stateTime, //
      TrajectoryControl trajectoryControl, //
      Tensor partitionScale, //
      FlowsInterface carFlows, //
      Tensor shape) {
    super(stateTime, trajectoryControl, partitionScale, carFlows, shape);
  }

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goal = goal.append(goalVelocity); // FIXME YN
    goalRegion = getGoalRegionWithDistance(goal);
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(goal, goalRadius);
    //  ---
    // costs with higher priority come first
    List<CostFunction> costs = new ArrayList<>();
    List<Double> slacks = new ArrayList<>();
    costs.addAll(costVector);
    slacks.addAll(slackVector);
    // ---
    if (timeCostPriority.isPresent() && timeCostSlack.isPresent()) {
      GlobalAssert.that(timeCostPriority.get() <= costs.size());
      slacks.add(timeCostPriority.get(), timeCostSlack.get());
      costs.add(timeCostPriority.get(), new Tse2MinTimeGoalManager(tse2ComboRegion, controls, MAX_SPEED));
    }
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, tse2ComboRegion);
    CTrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface);
    Comparator<Tensor> comparator = DiscretizedLexicographic.of(Tensors.vector(slacks));
    ((StandardTrajectoryPlanner) trajectoryPlanner).relabelDecision = //
        new LexicographicRelabelDecision(comparator);
    Comparator<GlcNode> comparator2 = new CustomNodeMeritComparator(comparator);
    trajectoryPlanner.queue = new PriorityQueue<>(comparator2);
    return trajectoryPlanner;
  }

  /** Sets the cost vector and their respective slacks. Lower indices have higher priority.
   * @param costVector
   * @param slackVector */
  public void setCostVector(List<CostFunction> costVector, List<Double> slackVector) {
    GlobalAssert.that(costVector.size() == slackVector.size());
    this.costVector = costVector;
    this.slackVector = slackVector;
  }

  /** Add time cost to the cost vector
   * @param priority
   * @param slack */
  public void addTimeCost(int priority, Double slack) {
    this.timeCostPriority = Optional.of(priority);
    this.timeCostSlack = Optional.of(slack);
  }
}
