// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.ref.d1h.Hermite1Subdivisions;
import ch.ethz.idsc.sophus.ref.d1h.Hermite2Subdivisions;
import ch.ethz.idsc.sophus.ref.d1h.Hermite3Subdivisions;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum HermiteSubdivisions {
  HERMITE1 {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite1Subdivisions.of(hsManifold, hsTransport, LAMBDA, MU);
    }
  },
  H1STANDARD {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite1Subdivisions.standard(hsManifold, hsTransport);
    }
  },
  /***************************************************/
  HERMITE2 {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.of(hsManifold, hsTransport, LAMBDA, MU);
    }
  },
  H2STANDARD {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.standard(hsManifold, hsTransport);
    }
  },
  H2MANIFOLD {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite2Subdivisions.manifold(hsManifold, hsTransport);
    }
  },
  /***************************************************/
  HERMITE3 {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsManifold, hsTransport, THETA, OMEGA);
    }
  },
  H3STANDARD {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsManifold, hsTransport);
    }
  },
  H3A1 {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a1(hsManifold, hsTransport);
    }
  },
  H3A2 {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a2(hsManifold, hsTransport);
    }
  },
  /***************************************************/
  HERMITE3_BM {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsManifold, hsTransport, biinvariantMean, THETA, OMEGA);
    }
  },
  H3STANDARD_BM {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.of(hsManifold, hsTransport, biinvariantMean);
    }
  },
  H3A1_BM {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a1(hsManifold, hsTransport, biinvariantMean);
    }
  },
  H3A2_BM {
    @Override
    public HermiteSubdivision supply(HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean) {
      return Hermite3Subdivisions.a2(hsManifold, hsTransport, biinvariantMean);
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
      HsManifold hsManifold, HsTransport hsTransport, BiinvariantMean biinvariantMean);
}
