// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class Clothoid3Display extends ClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Clothoid3Display();

  // ---
  private Clothoid3Display() {
    // ---
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Clothoid3.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "Cl3";
  }
}
