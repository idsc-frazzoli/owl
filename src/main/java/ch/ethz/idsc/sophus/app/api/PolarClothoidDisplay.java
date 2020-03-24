// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.clothoid.PolarClothoids;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

public class PolarClothoidDisplay extends AbstractClothoidDisplay {
  public static final AbstractClothoidDisplay INSTANCE = new PolarClothoidDisplay();

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return PolarClothoids.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "ClPol";
  }
}
