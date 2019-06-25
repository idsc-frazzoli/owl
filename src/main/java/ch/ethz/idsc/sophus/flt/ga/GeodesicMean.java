// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Careful: the implementation only supports sequences with odd number of elements
 * 
 * projects a sequence of points to their geodesic center
 * 
 * Example: if the points are from R^n the center would simply be the mean */
public enum GeodesicMean {
  ;
  /** @param geodesicInterface
   * @return geodesic center operator with Dirichlet/constant weights */
  public static TensorUnaryOperator of(SplitInterface splitInterface) {
    return GeodesicCenter.of(splitInterface, SmoothingKernel.DIRICHLET);
  }
}
