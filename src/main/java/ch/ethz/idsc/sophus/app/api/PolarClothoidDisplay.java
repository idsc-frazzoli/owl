// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.sophus.crv.clothoid.Se2CoveringClothoids;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class PolarClothoidDisplay extends AbstractClothoidDisplay {
  public static final AbstractClothoidDisplay INSTANCE = new PolarClothoidDisplay();

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2CoveringClothoids.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Scalar parametricDistance(Tensor p, Tensor q) {
    return Se2Clothoids.INSTANCE.curve(p, q).length();
  }

  @Override // from Object
  public String toString() {
    return "ClPol";
  }
}
