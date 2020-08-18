// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilderImpl;
import ch.ethz.idsc.sophus.clt.par.ClothoidIntegrations;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum CustomClothoids {
  ;
  public static ClothoidBuilder of(Scalar lambda) {
    return new ClothoidBuilderImpl(CustomClothoidQuadratic.of(lambda), ClothoidIntegrations.ANALYTIC);
  }

  public static Clothoid of(Scalar lambda, Tensor p, Tensor q) {
    return of(lambda).curve(p, q);
  }
}
