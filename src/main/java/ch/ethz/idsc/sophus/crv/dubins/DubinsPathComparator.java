// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;

public enum DubinsPathComparator implements Comparator<DubinsPath> {
  LENGTH() {
    @Override
    public int compare(DubinsPath dubinsPath1, DubinsPath dubinsPath2) {
      return Scalars.compare(dubinsPath1.length(), dubinsPath2.length());
    }
  }, //
  CURVATURE() {
    @Override
    public int compare(DubinsPath dubinsPath1, DubinsPath dubinsPath2) {
      return Scalars.compare(dubinsPath1.curvature(), dubinsPath2.curvature());
    }
  };
}
