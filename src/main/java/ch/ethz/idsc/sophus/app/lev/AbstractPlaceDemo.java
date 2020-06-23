// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.List;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;

public abstract class AbstractPlaceDemo extends LogWeightingDemo {
  public AbstractPlaceDemo(List<GeodesicDisplay> list, List<LogWeighting> array) {
    super(true, list, array);
    // ---
    setMidpointIndicated(false);
  }
}
