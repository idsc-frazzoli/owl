// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.rrts.adapter.Reversal;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

public class BidirectionalRrts implements Rrts {
  private final Rrts forwardRrts;
  private final Rrts backwardRrts;
  private final RrtsNode root;
  private final Tensor goal;
  private Optional<RrtsNode> goalNode = Optional.empty();

  public BidirectionalRrts( //
      TransitionSpace transitionSpace, //
      Supplier<RrtsNodeCollection> rrtsNodeCollection, //
      TransitionRegionQuery obstacleQuery, //
      TransitionCostFunction transitionCostFunction, //
      Tensor root, Tensor goal) {
    forwardRrts = new DefaultRrts(transitionSpace, rrtsNodeCollection.get(), obstacleQuery, transitionCostFunction);
    backwardRrts = new DefaultRrts(Reversal.of(transitionSpace), rrtsNodeCollection.get(), obstacleQuery, transitionCostFunction);
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
    System.out.print("match: ");
    List<RrtsNode> toGoal = Nodes.listToRoot(parent);
    Tensor backwardRoot = Lists.getLast(toGoal).state();
    if (!backwardRoot.equals(goal))
      throw TensorRuntimeException.of(backwardRoot, goal);
    List<Optional<RrtsNode>> optionals = new ArrayList<>();
    for (RrtsNode node : toGoal)
      optionals.add(forwardRrts.insertAsNode(node.state(), k_nearest)); // FIXME multiple insertions of same node
    if (optionals.stream().allMatch(Optional::isPresent)) { // FIXME should always be true
      goalNode = Lists.getLast(optionals);
      System.out.println("success");
    } else
      System.out.println("fail");
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
