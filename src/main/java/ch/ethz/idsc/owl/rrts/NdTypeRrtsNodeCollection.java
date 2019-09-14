// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdEntry;
import ch.ethz.idsc.owl.data.nd.NdMap;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;

/** collection of rrts nodes backed by a n-dimensional uniform tree
 * data structure is dependent on RrtsNdType */
public final class NdTypeRrtsNodeCollection implements RrtsNodeCollection {
  public static RrtsNodeCollection of(NdType ndType, Tensor lbounds, Tensor ubounds) {
    return new NdTypeRrtsNodeCollection(ndType, lbounds, ubounds);
  }

  // ---
  private final NdType ndType;
  private final NdMap<RrtsNode> ndMap;

  private NdTypeRrtsNodeCollection(NdType ndType, Tensor lbounds, Tensor ubounds) {
    this.ndType = ndType;
    ndMap = new NdTreeMap<>(ndType.convert(lbounds), ndType.convert(ubounds), 5, 20); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.add(ndType.convert(rrtsNode.state()), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    NdCluster<RrtsNode> cluster = ndMap.buildCluster(ndType.ndCenterInterfaceEnd(end), k_nearest);
    // System.out.println("considered " + cluster.considered() + " " + ndMap.size());
    return cluster.stream() //
        .map(NdEntry::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    NdCluster<RrtsNode> cluster = ndMap.buildCluster(ndType.ndCenterInterfaceBeg(start), k_nearest);
    // System.out.println("considered " + cluster.considered() + " " + ndMap.size());
    return cluster.stream() //
        .map(NdEntry::value) //
        .collect(Collectors.toList());
  }
}
