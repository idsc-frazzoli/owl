// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnBiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnFastMean;
import ch.ethz.idsc.sophus.hs.hn.HnPhongMean;
import ch.ethz.idsc.tensor.sca.Chop;

/** RMF(p,t,w)[x] == w.t for w = IDC(p,x) */
/* package */ enum HnMeans implements Supplier<BiinvariantMean> {
  EXACT(HnBiinvariantMean.of(Chop._05)), //
  FAST(HnFastMean.INSTANCE), //
  PHONG(HnPhongMean.INSTANCE), //
  ;

  private final BiinvariantMean biinvariantMean;

  private HnMeans(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override
  public BiinvariantMean get() {
    return biinvariantMean;
  }
}
