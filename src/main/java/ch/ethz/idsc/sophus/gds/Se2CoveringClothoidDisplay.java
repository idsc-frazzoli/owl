// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.tensor.Tensor;

public final class Se2CoveringClothoidDisplay extends AbstractClothoidDisplay {
  public static final ManifoldDisplay INSTANCE = new Se2CoveringClothoidDisplay();

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
