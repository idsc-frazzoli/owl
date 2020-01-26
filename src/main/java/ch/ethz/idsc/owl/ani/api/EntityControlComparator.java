// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.Comparator;

/* package */ enum EntityControlComparator implements Comparator<EntityControl> {
  INSTANCE;

  @Override // from Comparator
  public int compare(EntityControl entityControl1, EntityControl entityControl2) {
    return entityControl1.getProviderRank().compareTo(entityControl2.getProviderRank());
  }
}
