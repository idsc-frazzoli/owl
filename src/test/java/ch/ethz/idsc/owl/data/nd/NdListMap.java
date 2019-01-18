// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

/** class for verification of {@link NdTreeMap} */
public class NdListMap<V> implements NdMap<V> {
  private final List<NdPair<V>> list = new ArrayList<>();

  @Override // from NdMap
  public void add(Tensor location, V value) {
    list.add(new NdPair<>(location, value));
  }

  @Override // from NdMap
  public int size() {
    return list.size();
  }

  @Override // from NdMap
  public NdCluster<V> buildCluster(NdCenterInterface ndCenter, int limit) {
    return new NdCluster<>(list, ndCenter, limit);
  }

  @Override // from NdMap
  public void clear() {
    list.clear();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }
}
