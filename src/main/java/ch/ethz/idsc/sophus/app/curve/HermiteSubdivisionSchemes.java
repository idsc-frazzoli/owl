// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.crv.subdiv.Hermite1Subdivision;
import ch.ethz.idsc.sophus.crv.subdiv.Hermite2Subdivision;
import ch.ethz.idsc.sophus.crv.subdiv.Hermite3Subdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;

/* package */ enum HermiteSubdivisionSchemes {
  HERMITE1() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return new Hermite1Subdivision(lieGroup, lieExponential);
    }
  }, //
  HERMITE2() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return new Hermite2Subdivision(lieGroup, lieExponential);
    }
  }, //
  HERMITE3() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return new Hermite3Subdivision(lieGroup, lieExponential, biinvariantMean);
    }
  }, //
  ;
  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @return */
  public abstract HermiteSubdivision supply( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean);
}
