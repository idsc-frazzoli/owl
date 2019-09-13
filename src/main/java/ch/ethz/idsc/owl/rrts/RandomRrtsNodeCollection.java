// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;

public class RandomRrtsNodeCollection implements RrtsNodeCollection {
  private final List<RrtsNode> nodes = new ArrayList<>();

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    nodes.add(rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return nodes.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    Collections.shuffle(nodes);
    return nodes.stream().filter(node -> !node.state().equals(end)).limit(k_nearest).collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }
}
