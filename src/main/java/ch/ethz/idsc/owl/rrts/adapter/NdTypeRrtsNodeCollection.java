// code by jph, gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.nd.NdMap;
import ch.ethz.idsc.tensor.opt.nd.NdMatch;
import ch.ethz.idsc.tensor.opt.nd.NdTreeMap;

/** collection of rrts nodes backed by a n-dimensional uniform tree
 * data structure is dependent on NdType */
public final class NdTypeRrtsNodeCollection implements RrtsNodeCollection {
  /** @param ndType
   * @param lbounds vector
   * @param ubounds vector
   * @return */
  public static RrtsNodeCollection of(NdType ndType, Tensor lbounds, Tensor ubounds) {
    return new NdTypeRrtsNodeCollection( //
        Objects.requireNonNull(ndType), //
        lbounds, ubounds);
  }

  // ---
  private final NdType ndType;
  private final NdMap<RrtsNode> ndMap;

  private NdTypeRrtsNodeCollection(NdType ndType, Tensor lbounds, Tensor ubounds) {
    this.ndType = ndType;
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
    return ndMap.cluster(ndType.ndCenterTo(end), k_nearest).stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return ndMap.cluster(ndType.ndCenterFrom(start), k_nearest).stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }
}
