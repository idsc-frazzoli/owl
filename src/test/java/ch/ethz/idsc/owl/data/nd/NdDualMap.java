// code by jph
package ch.ethz.idsc.owl.data.nd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

public class NdDualMap<V> implements NdMap<V> {
  private final NdTreeMap<V> ndTreeMap;
  private final NdListMap<V> ndListMap;

  public NdDualMap(Tensor lbounds, Tensor ubounds, int maxDensity, int maxDepth) {
    ndTreeMap = new NdTreeMap<>(lbounds, ubounds, maxDensity, maxDepth);
    ndListMap = new NdListMap<>();
  }

  @Override // from NdMap
  public void add(Tensor location, V value) {
    ndTreeMap.add(location, value);
    ndListMap.add(location, value);
  }

  @Override // from NdMap
  public int size() {
    return ndTreeMap.size();
  }

  @Override // from NdMap
  public NdCluster<V> buildCluster(NdCenterInterface ndCenter, int limit) {
    NdCluster<V> c1 = ndTreeMap.buildCluster(ndCenter, limit);
    NdCluster<V> c2 = ndListMap.buildCluster(ndCenter, limit);
    {
      Scalar s1 = c1.stream().sorted(NdEntryComparators.INCREASING).map(NdEntry::distance).reduce(Scalar::add).get();
      Scalar s2 = c2.stream().sorted(NdEntryComparators.INCREASING).map(NdEntry::distance).reduce(Scalar::add).get();
      if (!s1.equals(s2)) {
        System.out.println(s1);
        System.out.println(s2);
        throw TensorRuntimeException.of(s1, s2);
      }
    }
    return c1;
  }

  @Override // from NdMap
  public void clear() {
    ndTreeMap.clear();
    ndListMap.clear();
  }

  @Override // from NdMap
  public boolean isEmpty() {
    return size() == 0;
  }
}
