// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** projects a sequence of points to their geodesic center
 * 
 * Example: if the points are from R^n the center would simply be the mean */
public class GeodesicMean extends GeodesicCenter {
  public GeodesicMean(GeodesicInterface geodesicInterface) {
    super(geodesicInterface, GeodesicMean::window);
  }

  private static final Tensor window(int i) {
    int width = 2 * i + 1;
    Scalar weight = RationalScalar.of(1, width);
    return Tensors.vector(k -> weight, width);
  }
}
