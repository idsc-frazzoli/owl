// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.BoundedMinQueue;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class NodeTransition implements Comparable<NodeTransition> {
  private final RrtsNode rrtsNode;
  private final Transition transition;

  public NodeTransition(RrtsNode rrtsNode, Transition transition) {
    this.rrtsNode = rrtsNode;
    this.transition = transition;
  }

  @Override
  public int compareTo(NodeTransition some) {
    return Scalars.compare(transition.length(), some.transition.length());
  }

  public RrtsNode rrtsNode() {
    return rrtsNode;
  }
}

/** prohibitive runtime */
// TODO JPH introduce quick check if transition can outperform certain threshold
public class ExhaustiveNodeCollection implements RrtsNodeCollection {
  public static RrtsNodeCollection of(TransitionSpace transitionSpace) {
    return new ExhaustiveNodeCollection(transitionSpace);
  }

  // ---
  private final TransitionSpace transitionSpace;
  private final List<RrtsNode> list = new ArrayList<>();

  private ExhaustiveNodeCollection(TransitionSpace transitionSpace) {
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
      queue.offer(new NodeTransition(rrtsNode, transitionSpace.connect(rrtsNode, end)));
    return queue.stream().map(NodeTransition::rrtsNode).collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    Queue<NodeTransition> queue = BoundedMinQueue.of(k_nearest);
    for (RrtsNode rrtsNode : list)
      queue.offer(new NodeTransition(rrtsNode, transitionSpace.connect(RrtsNode.createRoot(start, RealScalar.ZERO), rrtsNode.state()))); // TODO GJOEl investigate if ReversalTransition
    return queue.stream().map(NodeTransition::rrtsNode).collect(Collectors.toList());
  }
}
