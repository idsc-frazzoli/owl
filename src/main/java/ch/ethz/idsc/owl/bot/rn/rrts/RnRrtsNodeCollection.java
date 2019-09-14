// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.nd.EuclideanNdCenter;
import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdEntry;
import ch.ethz.idsc.owl.data.nd.NdMap;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;

public final class RnRrtsNodeCollection implements RrtsNodeCollection {
  private final NdMap<RrtsNode> ndMap;

  /** @param lbounds vector
   * @param ubounds vector */
  public RnRrtsNodeCollection(Tensor lbounds, Tensor ubounds) {
    ndMap = new NdTreeMap<>(lbounds, ubounds, 5, 20); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.add(rrtsNode.state(), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    NdCluster<RrtsNode> cluster = ndMap.buildCluster(EuclideanNdCenter.of(end), k_nearest);
    return cluster.stream() //
        .map(NdEntry::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }
}
