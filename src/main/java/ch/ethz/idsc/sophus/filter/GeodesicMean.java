// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.math.CenterWindowSampler;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Careful: the implementation only supports sequences with odd number of elements
 * 
 * projects a sequence of points to their geodesic center
 * 
 * Example: if the points are from R^n the center would simply be the mean */
public enum GeodesicMean {
  ;
  private static final CenterWindowSampler CENTER_WINDOW_SAMPLER = new CenterWindowSampler(SmoothingKernel.DIRICHLET);

  /** @param geodesicInterface
   * @return geodesic center operator with Dirichlet/constant weights */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface) {
    return GeodesicCenter.of(geodesicInterface, CENTER_WINDOW_SAMPLER);
  }
}
