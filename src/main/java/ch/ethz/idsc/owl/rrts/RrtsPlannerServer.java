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
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.DefaultRrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.RrtsTrajectoryPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;

// TODO provide RrtsPlanner from function/supplier
// requires new solution for root and time
public abstract class RrtsPlannerServer implements RrtsTrajectoryPlanner {
  private final TransitionSpace transitionSpace;
  private final TransitionRegionQuery obstacleQuery;
  private final Scalar resolution;
  private final TransitionCostFunction costFunction;
  private final RrtsFlowTrajectoryGenerator flowTrajectoryGenerator;
  // ---
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();
  private RrtsNode root = null;
  private Scalar time;
  private RrtsPlanner rrtsPlanner = null;
  private List<TrajectorySample> trajectory = new ArrayList<>();
  private NavigableMap<Scalar, List<TrajectorySample>> potentialFutureTrajectories = new TreeMap<>(Scalars::compare);

  public RrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel) {
    this(transitionSpace, obstacleQuery, resolution, stateSpaceModel, LengthCostFunction.IDENTITY);
  }

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
    flowTrajectoryGenerator = new RrtsFlowTrajectoryGenerator(stateSpaceModel);
  }

  @Override // from TrajectoryPlanner
  public void insertRoot(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = //
        trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), stateTime.time());
    trajectory = trajectory().stream().filter(predicate).collect(Collectors.toList());
    potentialFutureTrajectories.clear();
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection(), obstacleQuery, costFunction);
    root = rrts.insertAsNode(Objects.requireNonNull(stateTime).state(), 5).get();
    time = stateTime.time();
    rrtsPlanner = new DefaultRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal));
  }

  private List<TrajectorySample> trajectory() {
    if (Objects.nonNull(rrtsPlanner) && rrtsPlanner.getBest().isPresent()) {
      RrtsNode best = rrtsPlanner.getBest().get();
      List<RrtsNode> sequence = Nodes.listFromRoot(best);
      potentialFutureTrajectories.put(best.costFromRoot(), //
          flowTrajectoryGenerator.createTrajectory(transitionSpace, sequence, time, resolution));
    }
    if (potentialFutureTrajectories.isEmpty())
      return this.trajectory;
    List<TrajectorySample> trajectory = new ArrayList<>(this.trajectory);
    trajectory.addAll(potentialFutureTrajectories.firstEntry().getValue());
    return trajectory;
  }



  @Override // from ExpandInterface
  public Optional<RrtsNode> pollNext() {
    return Objects.nonNull(rrtsPlanner) //
        ? rrtsPlanner.pollNext() //
        : Optional.empty();
  }

  @Override // from ExpandInterface
  public void expand(RrtsNode node) {
    if (Objects.nonNull(rrtsPlanner))
      rrtsPlanner.expand(node); // FIXME can get stuck here
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> getBest() {
    return Objects.nonNull(rrtsPlanner) //
        ? rrtsPlanner.getBest() //
        : Optional.empty();
  }

  @Override // from TrajectoryPlanner
  public Optional<RrtsNode> getBestOrElsePeek() {
    Optional<RrtsNode> optional = getBest();
    if (optional.isPresent())
      return optional;
    return getQueue().isEmpty() //
        ? Optional.empty() //
        : Optional.of(rrtsPlanner.getQueue().get(0));
  }

  @Override // from TrajectoryPlanner
  public Collection<RrtsNode> getQueue() {
    return Objects.nonNull(rrtsPlanner) //
        ? rrtsPlanner.getQueue() //
        : Collections.emptyList();
  }

  @Override // from RrtsTrajectoryPlanner
  public void checkConsistency() {
    RrtsNodes.costConsistency(root, transitionSpace, costFunction);
  }

  public void setState(StateTime stateTime) {
    Predicate<TrajectorySample> predicate = //
        trajectorySample -> Scalars.lessEquals(stateTime.time(), trajectorySample.stateTime().time());
    trajectory = trajectory.stream().filter(predicate).collect(Collectors.toList());
    state = stateTime.state();
  }

  public void setGoal(Tensor goal) {
    this.goal = goal;
  }

  public Optional<RrtsNode> getRoot() {
    return Optional.ofNullable(root);
  }

  public Optional<List<TrajectorySample>> getTrajectory() {
    List<TrajectorySample> trajectory = trajectory();
    return trajectory.isEmpty() ? Optional.empty() : Optional.of(trajectory);
  }

  public Optional<TransitionRegionQuery> getObstacleQuery() {
    return Objects.nonNull(rrtsPlanner) //
        ? Optional.of(rrtsPlanner.getObstacleQuery()) //
        : Optional.empty();
  }

  public TransitionSpace getTransitionSpace() {
    try {
      return Serialization.copy(transitionSpace);
    } catch (Exception e) {
      // this should never happen!
      e.printStackTrace();
      return null;
    }
  }

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract RandomSampleInterface goalSampler(Tensor state);
}
