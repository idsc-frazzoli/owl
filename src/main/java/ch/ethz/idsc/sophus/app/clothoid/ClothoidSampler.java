// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.LagrangeQuadraticD;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.EqualizingDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;

/* package */ enum ClothoidSampler {
  ;
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 120);

  public static Tensor of(Clothoid clothoid) {
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    InverseCDF inverseCDF = //
        (InverseCDF) EqualizingDistribution.fromUnscaledPDF(DOMAIN.map(lagrangeQuadraticD).map(Scalar::abs));
    Tensor params = DOMAIN.map(inverseCDF::quantile).divide(RealScalar.of(DOMAIN.length()));
    return params.map(clothoid);
  }
}
