// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;

public abstract class RrtsPlannerServer {
  private final TransitionSpace transitionSpace;
  private final TransitionRegionQuery obstacleQuery;
  private final Scalar resolution;
  private final TransitionCostFunction costFunction;
  private final RrtsFlowTrajectoryGenerator flowTrajectoryGenerator;
  // ---
  private StateTime tail = null;
  private RrtsNode root = null;
  private RrtsPlanner rrtsPlanner = null;
  private List<TrajectorySample> trajectory = null;

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

  public RrtsPlannerProcess offer(StateTime tail) throws Exception {
    rrtsPlanner = null;
    trajectory = null;
    this.tail = Objects.requireNonNull(tail);
    return setup();
  }

  protected RrtsPlannerProcess setup() throws Exception{
    if (Objects.nonNull(tail)) {
      Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection(), obstacleQuery, costFunction);
      root = rrts.insertAsNode(tail.state(), 5).get();
      rrtsPlanner = new RrtsPlanner(rrts, spaceSampler(), goalSampler());
      return new RrtsPlannerProcess() {
        @Override // from RrtsPlannerProcess
        public void run(int steps) throws Exception {
          if (Objects.nonNull(rrtsPlanner)) {
            Expand.steps(rrtsPlanner, steps);
            RrtsNodes.costConsistency(root, transitionSpace, costFunction);
            if (rrtsPlanner.getBest().isPresent()) {
              RrtsNode best = rrtsPlanner.getBest().get();
              List<RrtsNode> sequence = Nodes.listFromRoot(best);
              trajectory = flowTrajectoryGenerator.createTrajectory(transitionSpace, sequence, tail.time(), resolution);
            }
          } else
            throw new Exception("no RRT* planner present");
        }
      };
    } else
      throw new Exception("failed to setup RRT* planner; no tail provided to expand from");
  }

  public TransitionRegionQuery getObstacleQuery() {
    return obstacleQuery;
  }

  public Optional<RrtsNode> getRoot() {
    return Optional.ofNullable(root);
  }

  public Optional<RrtsPlanner> getRrtsPlanner() {
    return Optional.ofNullable(rrtsPlanner);
  }

  public Optional<List<TrajectorySample>> getTrajectory() {
    return Optional.ofNullable(trajectory);
  }

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler();

  protected abstract RandomSampleInterface goalSampler();
}
