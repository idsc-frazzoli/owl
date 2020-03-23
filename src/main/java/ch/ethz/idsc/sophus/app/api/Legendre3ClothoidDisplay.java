// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.Legendre3Clothoids;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class Legendre3ClothoidDisplay extends ClothoidDisplay {
  public static final ClothoidDisplay INSTANCE = new Legendre3ClothoidDisplay();

  private Legendre3ClothoidDisplay() {
    // ---
  }

  @Override
  public GeodesicInterface geodesicInterface() {
    return Legendre3Clothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "ClLeg3";
  }
}
