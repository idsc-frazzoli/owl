// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RrtsPlannerServer implements TransitionPlanner {
  protected final TransitionSpace transitionSpace;
  protected final TransitionRegionQuery obstacleQuery;
  protected final TransitionCostFunction costFunction;
  private final Scalar resolution;
  private final RrtsFlowTrajectoryGenerator flowTrajectoryGenerator;
  // ---
  private Scalar time;
  private RrtsPlannerProcess process = null;
  private List<TrajectorySample> trajectory = new ArrayList<>();
  private NavigableMap<Scalar, List<TrajectorySample>> potentialFutureTrajectories = new TreeMap<>(Scalars::compare);

  public RrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    this.transitionSpace = transitionSpace;
    this.obstacleQuery = obstacleQuery;
    this.resolution = resolution;
    this.costFunction = costFunction;
    flowTrajectoryGenerator = new RrtsFlowTrajectoryGenerator(stateSpaceModel, this::uBetween); // TODO make uBetween dependent on state space model
  }

  @Override // from TrajectoryPlanner
  public void insertRoot(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = Trajectories.untilTime(stateTime.time());
    trajectory = Collections.unmodifiableList(trajectory().stream().filter(predicate).collect(Collectors.toList()));
    potentialFutureTrajectories.clear();
    time = stateTime.time();
    process = setupProcess(stateTime);
  }

  private List<TrajectorySample> trajectory() {
    if (Objects.nonNull(process) && process.rrtsPlanner.getBest().isPresent()) {
      RrtsNode best = process.rrtsPlanner.getBest().get();
      List<RrtsNode> sequence = Nodes.listFromRoot(best);
      potentialFutureTrajectories.put(best.costFromRoot(), //
          flowTrajectoryGenerator.createTrajectory(transitionSpace, sequence, time, resolution));
    }
    if (potentialFutureTrajectories.isEmpty())
      return Collections.unmodifiableList(trajectory);
    List<TrajectorySample> futureTrajectory = potentialFutureTrajectories.firstEntry().getValue();
    if (trajectory.isEmpty())
      return Collections.unmodifiableList(futureTrajectory);
    return Trajectories.glue(trajectory, futureTrajectory);
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> pollNext() {
    return Objects.nonNull(process) //
        ? process.rrtsPlanner.pollNext() //
        : Optional.empty();
  }

  @Override // from ExpandInterface
  public void expand(RrtsNode node) {
    if (Objects.nonNull(process))
      process.rrtsPlanner.expand(node); // FIXME GJOEL can get stuck here
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> getBest() {
    return Objects.nonNull(process) //
        ? process.rrtsPlanner.getBest() //
        : Optional.empty();
  }

  @Override // from TrajectoryPlanner
  public Optional<RrtsNode> getBestOrElsePeek() {
    Optional<RrtsNode> optional = getBest();
    if (optional.isPresent())
      return optional;
    return getQueue().isEmpty() //
        ? Optional.empty() //
        : Optional.of(process.rrtsPlanner.getQueue().get(0));
  }

  @Override // from TrajectoryPlanner
  public Collection<RrtsNode> getQueue() {
    return Objects.nonNull(process) //
        ? process.rrtsPlanner.getQueue() //
        : Collections.emptyList();
  }

  @Override // from RrtsTrajectoryPlanner
  public void checkConsistency() {
    RrtsNodes.costConsistency(process.root, transitionSpace, costFunction);
  }

  public void setState(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = Trajectories.afterTime(stateTime.time());
    trajectory = Collections.unmodifiableList(trajectory.stream().filter(predicate).collect(Collectors.toList()));
  }

  public Optional<RrtsNode> getRoot() {
    return Optional.ofNullable(process).map(RrtsPlannerProcess::root);
  }

  public Optional<List<TrajectorySample>> getTrajectory() {
    List<TrajectorySample> trajectory = trajectory();
    return trajectory.isEmpty() ? Optional.empty() : Optional.of(trajectory);
  }

  public Optional<TransitionRegionQuery> getObstacleQuery() {
    return Objects.nonNull(process) //
        ? Optional.of(process.rrtsPlanner.getObstacleQuery()) //
        : Optional.empty();
  }

  public TransitionSpace getTransitionSpace() {
    return transitionSpace;
  }

  protected abstract Tensor uBetween(StateTime orig, StateTime dest);

  protected abstract RrtsPlannerProcess setupProcess(StateTime stateTime);

  public abstract void setGoal(Tensor goal);

  protected static class RrtsPlannerProcess {
    private final RrtsPlanner rrtsPlanner;
    private final RrtsNode root;

    public RrtsPlannerProcess(RrtsPlanner rrtsPlanner, RrtsNode root) {
      this.rrtsPlanner = Objects.requireNonNull(rrtsPlanner);
      this.root = Objects.requireNonNull(root);
    }

    public RrtsPlanner planner() {
      return rrtsPlanner;
    }

    public RrtsNode root() {
      return root;
    }
  }
}
