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
      return Hermite1Subdivisions.of(lieGroup, lieExponential, LAMBDA, MU);
    }
  }, //
  H1STANDARD() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite1Subdivisions.of(lieGroup, lieExponential);
    }
  }, //
  HERMITE2() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.of(lieGroup, lieExponential, LAMBDA, MU);
    }
  }, //
  H2STANDARD() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.standard(lieGroup, lieExponential);
    }
  }, //
  H2MANIFOLD() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.manifold(lieGroup, lieExponential);
    }
  }, //
  HERMITE3() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(lieGroup, lieExponential, biinvariantMean, THETA, OMEGA);
    }
  }, //
  H3STANDARD() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(lieGroup, lieExponential, biinvariantMean);
    }
  }, //
  H3A1() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a1(lieGroup, lieExponential, biinvariantMean);
    }
  }, //
  H3A2() {
    @Override
    public HermiteSubdivision supply(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a2(lieGroup, lieExponential, biinvariantMean);
    }
  }, //
  ;
  public static Scalar LAMBDA = RationalScalar.of(-1, 8);
  public static Scalar MU = RationalScalar.of(-1, 2);
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
