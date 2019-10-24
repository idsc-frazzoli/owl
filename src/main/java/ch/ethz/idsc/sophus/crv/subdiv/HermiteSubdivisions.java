// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum HermiteSubdivisions {
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
      return new Hermite3Subdivision(lieGroup, lieExponential, biinvariantMean, THETA, OMEGA);
    }
  }, //
  ;
  public static Scalar THETA = RationalScalar.of(+1, 128);
  public static Scalar OMEGA = RationalScalar.of(-1, 16);

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @return
   * @throws Exception if either input parameter is null */
  public abstract HermiteSubdivision supply( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean);
}
