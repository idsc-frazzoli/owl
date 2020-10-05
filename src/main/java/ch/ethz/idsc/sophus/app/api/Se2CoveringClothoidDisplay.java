// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.tensor.Tensor;

public final class Se2CoveringClothoidDisplay extends AbstractClothoidDisplay {
  private static final long serialVersionUID = 4272438512167990990L;
  public static final GeodesicDisplay INSTANCE = new Se2CoveringClothoidDisplay();

  /***************************************************/
  private Se2CoveringClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidBuilder geodesicInterface() {
    return ClothoidBuilders.SE2_COVERING.clothoidBuilder();
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
