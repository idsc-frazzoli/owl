// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicDisplay {
  GeodesicInterface geodesicInterface();

  Tensor pointShape();

  Tensor project(Tensor xya);
}
