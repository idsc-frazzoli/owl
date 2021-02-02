// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum ClipCover {
  ;
  /** @param clip
   * @param scalar
   * @return */
  public static Clip of(Clip clip, Scalar scalar) {
    return Clips.interval( //
        Min.of(clip.min(), scalar), //
        Max.of(clip.max(), scalar));
  }
}
