// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.rrts.adapter.Reversal;
import ch.ethz.idsc.tensor.Tensor;

public class BidirectionalRrts implements Rrts {
  private final Rrts forwardRrts;
  private final Rrts backwardRrts;
  private final RrtsNode root;
  private final Tensor goal;
  private Optional<RrtsNode> goalNode = Optional.empty();

  public BidirectionalRrts( //
      TransitionSpace transitionSpace, //
      RrtsNodeCollection rrtsNodeCollection, //
      TransitionRegionQuery obstacleQuery, //
      TransitionCostFunction transitionCostFunction, //
      Tensor root, Tensor goal) {
    forwardRrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, obstacleQuery, transitionCostFunction);
    backwardRrts = new DefaultRrts(Reversal.of(transitionSpace), rrtsNodeCollection, obstacleQuery, transitionCostFunction);
    this.root = forwardRrts.insertAsNode(root, 0).get();
    backwardRrts.insertAsNode(goal, 0).get();
    this.goal = goal;
  }

  @Override // from Rrts
  public Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest) {
    Optional<RrtsNode> forwardOptional = forwardRrts.insertAsNode(state, k_nearest);
    Optional<RrtsNode> backwardOptional = backwardRrts.insertAsNode(state, k_nearest);
    if (forwardOptional.isPresent()) {
      backwardOptional.ifPresent(node -> rewireAround(node.parent(), k_nearest));
      return forwardOptional;
    }
    return backwardOptional;
  }

  @Override // from Rrts
  public void rewireAround(RrtsNode parent, int k_nearest) {
    Optional<RrtsNode> child = Optional.empty();
    while (Objects.nonNull(parent)) {
      child = forwardRrts.insertAsNode(parent.state(), k_nearest);
      if(!child.isPresent())
        break;
      parent = parent.parent();
    }
    if (child.map(node -> node.state().equals(goal)).orElse(false))
      goalNode = child;
  }

  @Override // from Rrts
  public int rewireCount() {
    return forwardRrts.rewireCount() + backwardRrts.rewireCount();
  }

  public RrtsNode getRoot() {
    return root;
  }

  public Optional<RrtsNode> getGoal() {
    return goalNode;
  }

  /* package */ TransitionRegionQuery getObstacleQuery() {
    return ((DefaultRrtsPlanner) forwardRrts).getObstacleQuery();
  }
}
