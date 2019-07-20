// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

// TODO JPH OWL 047 rename to ClothoidDisplay
public class Clothoid1Display extends ClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Clothoid1Display();

  // ---
  private Clothoid1Display() {
    // ---
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Clothoid.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "Cl";
  }
}
