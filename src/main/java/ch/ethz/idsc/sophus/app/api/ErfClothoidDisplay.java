// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.ErfClothoids;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class ErfClothoidDisplay extends ClothoidDisplay {
  public static final ClothoidDisplay INSTANCE = new ErfClothoidDisplay();

  private ErfClothoidDisplay() {
    // ---
  }

  @Override
  public GeodesicInterface geodesicInterface() {
    return ErfClothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "ClErf";
  }
}
