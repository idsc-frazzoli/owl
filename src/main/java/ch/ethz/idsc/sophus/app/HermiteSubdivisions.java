// code by jph
package ch.ethz.idsc.sophus.app;

import ch.ethz.idsc.sophus.crv.hermite.Hermite1Subdivisions;
import ch.ethz.idsc.sophus.crv.hermite.Hermite2Subdivisions;
import ch.ethz.idsc.sophus.crv.hermite.Hermite3Subdivisions;
import ch.ethz.idsc.sophus.crv.hermite.HermiteSubdivision;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum HermiteSubdivisions {
  HERMITE1() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite1Subdivisions.of(hsExponential, hsTransport, LAMBDA, MU);
    }
  }, //
  H1STANDARD() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite1Subdivisions.standard(hsExponential, hsTransport);
    }
  }, //
  /***************************************************/
  HERMITE2() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.of(hsExponential, hsTransport, LAMBDA, MU);
    }
  }, //
  H2STANDARD() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.standard(hsExponential, hsTransport);
    }
  }, //
  H2MANIFOLD() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.manifold(hsExponential, hsTransport);
    }
  }, //
  /***************************************************/
  HERMITE3() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsExponential, hsTransport, THETA, OMEGA);
    }
  }, //
  H3STANDARD() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsExponential, hsTransport);
    }
  }, //
  H3A1() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a1(hsExponential, hsTransport);
    }
  }, //
  H3A2() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a2(hsExponential, hsTransport);
    }
  }, //
  /***************************************************/
  HERMITE3_BM() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsExponential, hsTransport, biinvariantMean, THETA, OMEGA);
    }
  }, //
  H3STANDARD_BM() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsExponential, hsTransport, biinvariantMean);
    }
  }, //
  H3A1_BM() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a1(hsExponential, hsTransport, biinvariantMean);
    }
  }, //
  H3A2_BM() {
    @Override
    public HermiteSubdivision supply(HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a2(hsExponential, hsTransport, biinvariantMean);
    }
  };

  // TODO class design is no good
  public static Scalar LAMBDA = RationalScalar.of(-1, 8);
  public static Scalar MU = RationalScalar.of(-1, 2);
  public static Scalar THETA = RationalScalar.of(+1, 128);
  public static Scalar OMEGA = RationalScalar.of(-1, 16);

  /** @param lieGroup
   * @param exponential
   * @param biinvariantMean
   * @return
   * @throws Exception if either input parameter is null */
  public abstract HermiteSubdivision supply( //
      HsExponential hsExponential, HsTransport hsTransport, BiinvariantMean biinvariantMean);
}
