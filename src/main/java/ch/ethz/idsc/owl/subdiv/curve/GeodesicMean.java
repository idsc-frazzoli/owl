// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** projects a sequence of points to their geodesic center
 * 
 * Example: if the points are from R^n the center would simply be the mean */
public enum GeodesicMean {
  ;
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface) {
    return new GeodesicCenter(geodesicInterface, ConstantMask.FUNCTION);
  }
}
