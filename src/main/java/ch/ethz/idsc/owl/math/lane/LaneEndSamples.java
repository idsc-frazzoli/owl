// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.pdf.Distribution;

public enum LaneEndSamples {
  ;
  /** @param laneInterface
   * @param distribution
   * @return */
  // TODO function is very specific and should not need laneInterface as parameter
  public static RegionRandomSample spherical(LaneInterface laneInterface, Distribution distribution) {
    return new RegionRandomSample( //
        LaneRandomSample.of(laneInterface, distribution).around(laneInterface.midLane().length() - 1), //
        new SphericalRegion(Extract2D.FUNCTION.apply(Last.of(laneInterface.midLane())), Last.of(laneInterface.margins())), //
        Extract2D.FUNCTION);
  }
}
