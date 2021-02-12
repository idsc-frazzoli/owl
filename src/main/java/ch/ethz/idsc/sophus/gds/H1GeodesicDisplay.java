// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.tensor.Tensor;

public class H1GeodesicDisplay extends HnGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new H1GeodesicDisplay();

  /***************************************************/
  private H1GeodesicDisplay() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.copy();
  }
}
