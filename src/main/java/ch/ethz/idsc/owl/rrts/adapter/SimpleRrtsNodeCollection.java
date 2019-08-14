// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class SimpleRrtsNodeCollection implements RrtsNodeCollection {
  private final TransitionSpace transitionSpace;
  private final TransitionSpace reversalTransitionSpace;
  private final TransitionCostFunction transitionCostFunction;
  private final Set<RrtsNode> set = new HashSet<>();

  public SimpleRrtsNodeCollection(TransitionSpace transitionSpace, TransitionCostFunction transitionCostFunction) {
    this.transitionSpace = transitionSpace;
    reversalTransitionSpace = ReversalTransitionSpace.of(transitionSpace);
    this.transitionCostFunction = transitionCostFunction;
  }

  @Override
  public void insert(RrtsNode node) {
    set.add(node);
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    Comparator<RrtsNode> comparator = new Comparator<RrtsNode>() {
      final Map<RrtsNode, Scalar> map = new HashMap<>();

      @Override
      public int compare(RrtsNode o1, RrtsNode o2) {
        if (!map.containsKey(o1))
          map.put(o1, transitionCostFunction.cost(o1, transitionSpace.connect(o1.state(), end)));
        if (!map.containsKey(o2))
          map.put(o2, transitionCostFunction.cost(o2, transitionSpace.connect(o2.state(), end)));
        return Scalars.compare(map.get(o1), map.get(o2));
      }
    };
    PriorityQueue<RrtsNode> priorityQueue = new PriorityQueue<>(comparator);
    priorityQueue.addAll(set);
    return Stream.generate(priorityQueue::poll) //
        .limit(k_nearest) //
        .collect(Collectors.toList());
  }

  @Override
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    Comparator<RrtsNode> comparator = new Comparator<RrtsNode>() {
      final Map<RrtsNode, Scalar> map = new HashMap<>();

      @Override
      public int compare(RrtsNode o1, RrtsNode o2) {
        if (!map.containsKey(o1))
          map.put(o1, transitionCostFunction.cost(o1, reversalTransitionSpace.connect(o1.state(), start)));
        if (!map.containsKey(o2))
          map.put(o2, transitionCostFunction.cost(o2, reversalTransitionSpace.connect(o2.state(), start)));
        return Scalars.compare(map.get(o1), map.get(o2));
      }
    };
    PriorityQueue<RrtsNode> priorityQueue = new PriorityQueue<>(comparator);
    priorityQueue.addAll(set);
    return Stream.generate(priorityQueue::poll) //
        .limit(k_nearest) //
        .collect(Collectors.toList());
  }
}
