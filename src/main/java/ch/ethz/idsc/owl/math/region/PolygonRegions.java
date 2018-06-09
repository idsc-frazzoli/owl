// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

public enum PolygonRegions {
  ;
  /** @param polygon is mapped to numeric precision */
  public static Region<Tensor> numeric(Tensor polygon) {
    return new PolygonRegion(N.DOUBLE.of(polygon));
  }
}
