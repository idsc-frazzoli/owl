// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

public enum DubinsPathComparator {
  ;
  private static final Comparator<DubinsPath> LENGTH = //
      (dubinsPath1, dubinsPath2) -> Scalars.compare(dubinsPath1.length(), dubinsPath2.length());

  public static Comparator<DubinsPath> length() {
    return LENGTH;
  }

  private static final Comparator<DubinsPath> CURVATURE = //
      (dubinsPath1, dubinsPath2) -> Scalars.compare(dubinsPath1.curvature(), dubinsPath2.curvature());

  public static Comparator<DubinsPath> curvature() {
    return CURVATURE;
  }
}
