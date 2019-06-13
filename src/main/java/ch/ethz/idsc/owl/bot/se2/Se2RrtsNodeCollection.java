// code by gjoel
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdEntry;
import ch.ethz.idsc.owl.data.nd.NdMap;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;

/** collection of nodes in Se2 backed by a 2-dimensional uniform tree
 * uses euclidean distance in R^2 for the sake of speed */
public class Se2RrtsNodeCollection implements RrtsNodeCollection {
  private final NdMap<RrtsNode> ndMap;

  public Se2RrtsNodeCollection(Tensor lbounds, Tensor ubounds) {
    ndMap = new NdTreeMap<>(Extract2D.FUNCTION.apply(lbounds), Extract2D.FUNCTION.apply(ubounds), 5, 20); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.add(Extract2D.FUNCTION.apply(rrtsNode.state()), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    NdCenterInterface distanceInterface = NdCenterInterface.euclidean(Extract2D.FUNCTION.apply(end));
    NdCluster<RrtsNode> cluster = ndMap.buildCluster(distanceInterface, k_nearest);
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
