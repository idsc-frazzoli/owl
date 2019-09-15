// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.BoundedMinQueue;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** Careful: implementation is only for testing purposes
 * 
 * implementation performs a complete sweep through the collection
 * the runtime is prohibitive */
public class ExhaustiveRrtsNodeCollection implements RrtsNodeCollection {
  /** @param transitionSpace non-null
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace) {
    return new ExhaustiveRrtsNodeCollection(Objects.requireNonNull(transitionSpace));
  }

  private static class NodeTransition implements Comparable<NodeTransition> {
    private final RrtsNode rrtsNode;
    private final Transition transition;

    public NodeTransition(RrtsNode rrtsNode, Transition transition) {
      this.rrtsNode = rrtsNode;
      this.transition = transition;
    }

    @Override // from Comparable
    public int compareTo(NodeTransition some) {
      return Scalars.compare(transition.length(), some.transition.length());
    }

    public RrtsNode rrtsNode() {
      return rrtsNode;
    }
  }

  // ---
  private final List<RrtsNode> list = new LinkedList<>();
  private final TransitionSpace transitionSpace;

  private ExhaustiveRrtsNodeCollection(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    list.add(rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return list.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    Queue<NodeTransition> queue = BoundedMinQueue.of(k_nearest);
    for (RrtsNode rrtsNode : list)
      queue.offer(new NodeTransition(rrtsNode, transitionSpace.connect(rrtsNode.state(), end)));
    return queue.stream().map(NodeTransition::rrtsNode).collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    Queue<NodeTransition> queue = BoundedMinQueue.of(k_nearest);
    for (RrtsNode rrtsNode : list)
      queue.offer(new NodeTransition(rrtsNode, transitionSpace.connect(start, rrtsNode.state())));
    return queue.stream().map(NodeTransition::rrtsNode).collect(Collectors.toList());
  }
}
