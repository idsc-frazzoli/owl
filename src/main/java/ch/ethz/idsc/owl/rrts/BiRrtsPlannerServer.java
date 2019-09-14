// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Objects;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.core.BiRrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.BidirectionalRrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class BiRrtsPlannerServer extends RrtsPlannerServer {
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();

  public BiRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
  }

  @Override // from RrtsPlannerServer
  public void setState(StateTime stateTime) {
    super.setState(stateTime);
    state = stateTime.state();
  }

  @Override // from RrtsPlannerServer
  public void setGoal(Tensor goal) {
    this.goal = convertGoal(goal);
  }

  @Override // from RrtsPlannerServer
  protected RrtsPlannerProcess setupProcess(StateTime stateTime) {
    BidirectionalRrts rrts = new BidirectionalRrts(getTransitionSpace(), this::rrtsNodeCollection, obstacleQuery, costFunction,
        Objects.requireNonNull(stateTime).state(), goal);
    RrtsPlanner rrtsPlanner = new BiRrtsPlanner(rrts, spaceSampler(state));
    return new RrtsPlannerProcess(rrtsPlanner, rrts.getRoot());
  }

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract Tensor convertGoal(Tensor goal);
}
