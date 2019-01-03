// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

class NodeTransition implements Comparable<NodeTransition> {
  final RrtsNode rrtsNode;
  private final Transition transition;

  public NodeTransition(RrtsNode rrtsNode, Transition transition) {
    this.rrtsNode = rrtsNode;
    this.transition = transition;
  }

  @Override
  public int compareTo(NodeTransition some) {
    return Scalars.compare(transition.length(), some.transition.length());
  }
}

/** prohibitive runtime */
// TODO JPH a lot of improvements are possible
// TODO JPH introduce quick check if transition can outperform certain threshold
public class ExhaustiveNodeCollection implements RrtsNodeCollection {
  private final TransitionSpace transitionSpace;
  private final List<RrtsNode> list = new ArrayList<>();

  public ExhaustiveNodeCollection(TransitionSpace transitionSpace) {
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
    Queue<NodeTransition> queue = new PriorityQueue<>();
    for (RrtsNode rrtsNode : list)
      queue.add(new NodeTransition(rrtsNode, transitionSpace.connect(rrtsNode.state(), end)));
    Iterator<NodeTransition> iterator = queue.iterator();
    List<RrtsNode> best = new LinkedList<>();
    while (iterator.hasNext() && 0 <= --k_nearest) {
      NodeTransition nodeTransition = iterator.next();
      best.add(nodeTransition.rrtsNode);
    }
    return best;
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    Queue<NodeTransition> queue = new PriorityQueue<>();
    for (RrtsNode rrtsNode : list)
      queue.add(new NodeTransition(rrtsNode, transitionSpace.connect(start, rrtsNode.state())));
    Iterator<NodeTransition> iterator = queue.iterator();
    List<RrtsNode> best = new LinkedList<>();
    while (iterator.hasNext() && 0 <= --k_nearest) {
      NodeTransition nodeTransition = iterator.next();
      best.add(nodeTransition.rrtsNode);
    }
    return best;
  }
}
