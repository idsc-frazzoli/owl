// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Comparator;

public enum EntityControlComparator implements Comparator<EntityControl> {
  INSTANCE;
  // ---
  @Override
  public int compare(EntityControl entityControl1, EntityControl entityControl2) {
    return entityControl1.getProviderRank().compareTo(entityControl2.getProviderRank());
  }
}
