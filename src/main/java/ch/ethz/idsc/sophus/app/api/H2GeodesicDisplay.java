// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;

public class H2GeodesicDisplay extends HnGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new H2GeodesicDisplay();

  /***************************************************/
  private H2GeodesicDisplay() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, 2));
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
  }
}
