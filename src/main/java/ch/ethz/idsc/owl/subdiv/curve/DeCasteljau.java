// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** algorithm for the evaluation of Bezier curves
 * 
 * https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm */
/* package */ class DeCasteljau {
  private final GeodesicInterface geodesicInterface;

  public DeCasteljau(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  public Tensor of(Tensor points, Scalar scalar) {
    while (1 < points.length()) {
      Tensor tensor = Tensors.empty();
      Tensor p = points.get(0);
      for (int index = 1; index < points.length(); ++index) {
        Tensor q = points.get(index);
        tensor.append(geodesicInterface.split(p, q, scalar));
        p = q;
      }
      points = tensor;
    }
    return points.get(0);
  }
}
