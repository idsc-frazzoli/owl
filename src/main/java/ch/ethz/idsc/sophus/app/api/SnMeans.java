// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.sn.SnFastMean;
import ch.ethz.idsc.sophus.hs.sn.SnMean;
import ch.ethz.idsc.sophus.hs.sn.SnPhongMean;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.sca.Chop;

/** RMF(p,t,w)[x] == w.t for w = IDC(p,x) */
public enum SnMeans implements Supplier<BiinvariantMean> {
  EXACT(new SnMean(Chop._05)), //
  FAST(SnFastMean.INSTANCE), //
  PHONG(SnPhongMean.INSTANCE), //
  ;

  private final BiinvariantMean biinvariantMean;

  private SnMeans(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override
  public BiinvariantMean get() {
    return biinvariantMean;
  }
}
