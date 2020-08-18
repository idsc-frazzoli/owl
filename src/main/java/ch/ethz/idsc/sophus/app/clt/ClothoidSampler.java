// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum ClothoidSampler {
  ;
  public static Tensor of(Clothoid clothoid) {
    return ClothoidTransition.linearized(clothoid, RealScalar.of(0.1));
  }
}
