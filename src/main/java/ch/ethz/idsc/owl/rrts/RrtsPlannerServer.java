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

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
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

// TODO find more elegant implementation
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
  private RrtsPlanner rrtsPlanner = null;
  private List<TrajectorySample> trajectory = new ArrayList<>();
  private NavigableMap<Scalar, List<TrajectorySample>> potentialFutureTrajectories = new TreeMap<>(Scalars::compare);
  private RrtsPlannerProcess process = null;

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

  protected void from(StateTime tail) {
    process = null;
    if (Objects.nonNull(tail)) {
      Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection(), obstacleQuery, costFunction);
      root = rrts.insertAsNode(tail.state(), 5).get();
      rrtsPlanner = new RrtsPlanner(rrts, spaceSampler(state), goalSampler(goal));
      process = new RrtsPlannerProcess() {
        @Override // from RrtsPlannerProcess
        public void run(int steps) {
          if (Objects.nonNull(rrtsPlanner)) {
            Expand.steps(rrtsPlanner, steps);
            RrtsNodes.costConsistency(root, transitionSpace, costFunction);
            if (rrtsPlanner.getBest().isPresent()) {
              RrtsNode best = rrtsPlanner.getBest().get();
              List<RrtsNode> sequence = Nodes.listFromRoot(best);
              potentialFutureTrajectories.put(best.costFromRoot(), //
                  flowTrajectoryGenerator.createTrajectory(transitionSpace, sequence, tail.time(), resolution));
            }
          }
        }
      };
    }
  }

  private List<TrajectorySample> trajectory() {
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
      rrtsPlanner.expand(node);
  }

  @Override // from ExpandInterface
  public Optional<RrtsNode> getBest() {
    return Objects.nonNull(rrtsPlanner) //
        ? rrtsPlanner.getBest() //
        : Optional.empty();
  }

  @Override // from TrajectoryPlanner
  public void insertRoot(StateTime stateTime) {
    rrtsPlanner = null;
    trajectory = trajectory();
    potentialFutureTrajectories.clear();
    from(Objects.requireNonNull(stateTime));
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
  public Optional<RrtsPlannerProcess> getProcess() {
    return Optional.ofNullable(process);
  }

  public void setState(Tensor state) {
    this.state = state;
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

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract RandomSampleInterface goalSampler(Tensor state);
}
