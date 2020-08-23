// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilderImpl;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegrations;
import ch.ethz.idsc.tensor.Scalar;

/* package */ enum CustomClothoidBuilder {
  ;
  public static ClothoidBuilder of(Scalar lambda) {
    return new ClothoidBuilderImpl(CustomClothoidQuadratic.of(lambda), ClothoidIntegrations.ANALYTIC);
  }
}
