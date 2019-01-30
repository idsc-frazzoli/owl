// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;

/* package */ abstract class DatasetFilterDemo extends GeodesicDisplayDemo {
  public DatasetFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
  }
}
