// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.rrts.adapter.ReversalTransitionSpace;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

public class BidirectionalRrts implements Rrts {
  private final RrtsNodeCollection nodeCollection;
  private final Rrts forwardRrts;
  private final Rrts backwardRrts;
  private final RrtsNode root;
  private final Tensor goal;

  public BidirectionalRrts( //
      TransitionSpace transitionSpace, //
      Supplier<RrtsNodeCollection> rrtsNodeCollection, //
      TransitionRegionQuery obstacleQuery, //
      TransitionCostFunction transitionCostFunction, //
      Tensor root, Tensor goal) {
    nodeCollection = rrtsNodeCollection.get();
    forwardRrts = new DefaultRrts(transitionSpace, nodeCollection, obstacleQuery, transitionCostFunction);
    backwardRrts = new DefaultRrts(ReversalTransitionSpace.of(transitionSpace), rrtsNodeCollection.get(), obstacleQuery, transitionCostFunction);
    this.root = forwardRrts.insertAsNode(root, 0).get();
    forwardRrts.insertAsNode(goal, 1); // trivial solution
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
    List<RrtsNode> toGoal = Nodes.listToRoot(parent);
    Tensor backwardRoot = Lists.getLast(toGoal).state();
    if (!backwardRoot.equals(goal))
      throw TensorRuntimeException.of(backwardRoot, goal);
    for (RrtsNode node : toGoal) {
      Optional<RrtsNode> optional = find(node.state());
      if (!optional.isPresent())
        ((DefaultRrts) forwardRrts).insertAsNode(node.state(), k_nearest, true).orElseThrow(NoSuchElementException::new);
    }
  }

  @Override // from Rrts
  public int rewireCount() {
    return forwardRrts.rewireCount() + backwardRrts.rewireCount();
  }

  public RrtsNode getRoot() {
    return root;
  }

  public Optional<RrtsNode> getGoal() {
    return find(goal);
  }

  /* package */ TransitionRegionQuery getObstacleQuery() {
    return ((DefaultRrtsPlanner) forwardRrts).getObstacleQuery();
  }

  private Optional<RrtsNode> find(Tensor state) {
    Optional<RrtsNode> optional = nodeCollection.nearTo(state, 1).stream().findFirst();
    if (optional.isPresent()) {
      RrtsNode closest = optional.get();
      if (closest.state().equals(state))
        return optional;
    }
    return Optional.empty();
  }
}
