// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public enum ClipIntersect {
  ;
  /** @param clip1
   * @param clip2
   * @return
   * @throws Exception if resulting intersection is empty */
  public static Clip of(Clip clip1, Clip clip2) {
    return Clips.interval( //
        Max.of(clip1.min(), clip2.min()), //
        Min.of(clip1.max(), clip2.max()));
  }
}
