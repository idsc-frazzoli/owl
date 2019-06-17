// code by gjoel
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdEntry;
import ch.ethz.idsc.owl.data.nd.NdMap;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;

/** collection of nodes in Se2 backed by a n-dimensional uniform tree
 * data structure is dependent on Se2NdType */
public class Se2RrtsNodeCollection implements RrtsNodeCollection {
  public static Se2RrtsNodeCollection euclidean(Tensor lbounds, Tensor ubounds) {
    return new Se2RrtsNodeCollection(Se2NdType.EUCLIDEAN, lbounds, ubounds);
  }

  public static Se2RrtsNodeCollection clothoid(Tensor lbounds, Tensor ubounds) {
    return new Se2RrtsNodeCollection(Se2NdType.CLOTHOID, lbounds, ubounds);
  }

  // ---
  private final Se2NdType type;
  private final NdMap<RrtsNode> ndMap;

  private Se2RrtsNodeCollection(Se2NdType type, Tensor lbounds, Tensor ubounds) {
    this.type = type;
    ndMap = new NdTreeMap<>(this.type.convert(lbounds), this.type.convert(ubounds), 5, 20); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.add(type.convert(rrtsNode.state()), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    NdCluster<RrtsNode> cluster = ndMap.buildCluster(type.getNdCenterInterface(end), k_nearest);
    // System.out.println("considered " + cluster.considered() + " " + ndMap.size());
    return cluster.stream() //
        .map(NdEntry::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }
}
