// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class ClothoidDisplay extends AbstractClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new ClothoidDisplay();

  private ClothoidDisplay() {
    // ---
  }

  @Override
  public GeodesicInterface geodesicInterface() {
    return Clothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "Cl";
  }
}
