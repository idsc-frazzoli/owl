// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;

public class H1GeodesicDisplay extends HnGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new H1GeodesicDisplay();

  /***************************************************/
  private H1GeodesicDisplay() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, 1));
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.copy();
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
  }
}
