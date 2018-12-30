// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

public enum DubinsPathLengthComparator implements Comparator<DubinsPath> {
  INSTANCE;
  // ---
  @Override // from Comparator
  public int compare(DubinsPath dubinsPath1, DubinsPath dubinsPath2) {
    return Scalars.compare(dubinsPath1.length(), dubinsPath2.length());
  }
}
