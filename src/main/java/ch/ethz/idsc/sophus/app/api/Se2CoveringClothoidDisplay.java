// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.Se2CoveringClothoidBuilder;
import ch.ethz.idsc.tensor.Tensor;

public final class Se2CoveringClothoidDisplay extends AbstractClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2CoveringClothoidDisplay();

  /***************************************************/
  private Se2CoveringClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidBuilder geodesicInterface() {
    return Se2CoveringClothoidBuilder.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return xya;
  }

  @Override // from Object
  public String toString() {
    return "ClC";
  }
}
