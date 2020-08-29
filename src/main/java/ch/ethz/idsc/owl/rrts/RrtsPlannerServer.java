// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.data.tree.ObservingExpandInterface;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RrtsPlannerServer implements TransitionPlanner, ObservingExpandInterface<RrtsNode> {
  private final TransitionSpace transitionSpace;
  protected final TransitionRegionQuery obstacleQuery;
  protected final TransitionCostFunction costFunction;
  // ---
  private final Scalar resolution;
  private final RrtsFlowTrajectoryGenerator flowTrajectoryGenerator;
  // ---
  private Scalar time;
  private RrtsPlannerProcess _rrtsPlannerProcess = null;
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
    // TODO GJOEL make uBetween dependent on state space model, or input uBetween as function pointer to planner server
    flowTrajectoryGenerator = new RrtsFlowTrajectoryGenerator(stateSpaceModel, this::uBetween);
  }

  @Override // from TrajectoryPlanner
  public final void insertRoot(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = Trajectories.untilTime(stateTime.time());
    trajectory = Collections.unmodifiableList(trajectory().stream().filter(predicate).collect(Collectors.toList()));
    potentialFutureTrajectories.clear();
    time = stateTime.time();
    _rrtsPlannerProcess = setupProcess(stateTime);
  }

  private List<TrajectorySample> trajectory() {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    if (Objects.nonNull(rrtsPlannerProcess) && //
        rrtsPlannerProcess.planner().getBest().isPresent()) {
      RrtsNode best = rrtsPlannerProcess.planner().getBest().get();
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
  public final Optional<RrtsNode> pollNext() {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    return Objects.nonNull(rrtsPlannerProcess) //
        ? rrtsPlannerProcess.planner().pollNext() //
        : Optional.empty();
  }

  @Override // from ExpandInterface
  public final void expand(RrtsNode node) {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    if (Objects.nonNull(rrtsPlannerProcess))
      rrtsPlannerProcess.planner().expand(node); // FIXME GJOEL can get stuck here
  }

  @Override // from ExpandInterface
  public final Optional<RrtsNode> getBest() {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    return Objects.nonNull(rrtsPlannerProcess) //
        ? rrtsPlannerProcess.planner().getBest() //
        : Optional.empty();
  }

  @Override // from TrajectoryPlanner
  public final Optional<RrtsNode> getBestOrElsePeek() {
    Optional<RrtsNode> optional = getBest();
    if (optional.isPresent())
      return optional;
    Iterator<RrtsNode> iterator = getQueue().iterator();
    return iterator.hasNext() //
        ? Optional.of(iterator.next())
        : Optional.empty();
  }

  @Override // from TrajectoryPlanner
  public Collection<RrtsNode> getQueue() {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    return Objects.nonNull(rrtsPlannerProcess) //
        ? rrtsPlannerProcess.planner().getQueue() //
        : Collections.emptyList();
  }

  @Override // from RrtsTrajectoryPlanner
  public final void checkConsistency() {
    // FIXME GJOEL _rrtsPlannerProcess may be null
    RrtsNodes.costConsistency(_rrtsPlannerProcess.root(), transitionSpace, costFunction);
  }

  public final Optional<RrtsNode> getRoot() {
    return Optional.ofNullable(_rrtsPlannerProcess).map(RrtsPlannerProcess::root);
  }

  public final Optional<List<TrajectorySample>> getTrajectory() {
    List<TrajectorySample> trajectory = trajectory();
    return trajectory.isEmpty() ? Optional.empty() : Optional.of(trajectory);
  }

  public final Optional<TransitionRegionQuery> getObstacleQuery() {
    RrtsPlannerProcess rrtsPlannerProcess = _rrtsPlannerProcess;
    return Objects.nonNull(rrtsPlannerProcess) //
        ? Optional.of(rrtsPlannerProcess.planner().getObstacleQuery()) //
        : Optional.empty();
  }

  public final TransitionSpace getTransitionSpace() {
    return transitionSpace;
  }

  public final void addTrajectoryPostprocessing(CurveSubdivision curveSubdivision, TensorMetric tensorMetric) {
    flowTrajectoryGenerator.addPostProcessing(curveSubdivision, tensorMetric);
  }

  /** @param stateTime */
  public void setState(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = Trajectories.afterTime(stateTime.time());
    trajectory = Collections.unmodifiableList(trajectory.stream().filter(predicate).collect(Collectors.toList()));
  }

  /** @param orig
   * @param dest
   * @return */
  protected abstract Tensor uBetween(StateTime orig, StateTime dest);

  /** @param stateTime
   * @return */
  protected abstract RrtsPlannerProcess setupProcess(StateTime stateTime);

  /** @param goal */
  public abstract void setGoal(Tensor goal);
}
