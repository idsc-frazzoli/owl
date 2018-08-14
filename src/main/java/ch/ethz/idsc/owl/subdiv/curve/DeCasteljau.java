// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** algorithm for the evaluation of Bezier curves
 * 
 * https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm */
/* package */ class DeCasteljau implements ScalarTensorFunction {
  private final GeodesicInterface geodesicInterface;
  private final Tensor points;

  public DeCasteljau(GeodesicInterface geodesicInterface, Tensor points) {
    this.geodesicInterface = geodesicInterface;
    this.points = points;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    Tensor points = this.points;
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
