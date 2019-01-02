// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicDisplay {
  /** @return */
  GeodesicInterface geodesicInterface();

  /** @return polygon */
  Tensor shape();

  /** @param xya
   * @return coordinates of control point */
  Tensor project(Tensor xya);

  /** @param p
   * @return 3x3 matrix */
  Tensor matrixLift(Tensor p);

  /** @return lie group if the space is a lie group, otherwise null */
  LieGroup lieGroup();

  @Override
  String toString();
}
