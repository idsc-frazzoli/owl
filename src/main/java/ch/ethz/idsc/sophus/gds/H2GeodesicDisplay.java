// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.tensor.Tensor;

public class H2GeodesicDisplay extends HnGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new H2GeodesicDisplay();

  /***************************************************/
  private H2GeodesicDisplay() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return new H2ArrayPlot(RADIUS);
  }
}
