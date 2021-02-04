// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.nd.EuclideanNdCenter;
import ch.ethz.idsc.tensor.opt.nd.NdMap;
import ch.ethz.idsc.tensor.opt.nd.NdMatch;
import ch.ethz.idsc.tensor.opt.nd.NdTreeMap;

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
    Collection<NdMatch<RrtsNode>> cluster = ndMap.cluster(EuclideanNdCenter.of(end), k_nearest);
    return cluster.stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }
}
