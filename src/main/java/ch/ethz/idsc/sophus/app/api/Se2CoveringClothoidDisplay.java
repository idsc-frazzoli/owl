// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidInterface;
import ch.ethz.idsc.sophus.crv.clothoid.Se2CoveringClothoids;

public final class Se2CoveringClothoidDisplay extends AbstractClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2CoveringClothoidDisplay();

  private Se2CoveringClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidInterface geodesicInterface() {
    return Se2CoveringClothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "ClPol";
  }
}
