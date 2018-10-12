// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sig.WindowFunctions;

/** Careful: the implementation only supports sequences with odd number of elements
 * 
 * projects a sequence of points to their geodesic center
 * 
 * Example: if the points are from R^n the center would simply be the mean */
public enum GeodesicMean {
  ;
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface) {
    return GeodesicCenter.of(geodesicInterface, WindowFunctions.DIRICHLET);
  }
}
