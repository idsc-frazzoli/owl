// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidInterface;
import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;

public final class Se2ClothoidDisplay extends AbstractClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2ClothoidDisplay();

  private Se2ClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidInterface geodesicInterface() {
    return Se2Clothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "Cl";
  }
}
