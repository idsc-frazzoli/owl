// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class Clothoid2Display extends ClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Clothoid2Display();

  // ---
  private Clothoid2Display() {
    // ---
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Clothoid2.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "Cl2";
  }
}
