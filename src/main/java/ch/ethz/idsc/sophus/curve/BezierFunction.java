// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;

/** De Casteljau's algorithm for the evaluation of Bezier curves
 * 
 * https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm */
public class BezierFunction implements ScalarTensorFunction {
  /** @param geodesicInterface
   * @param control
   * @return function parameterized by the interval [0, 1] */
  public static ScalarTensorFunction of(GeodesicInterface geodesicInterface, Tensor control) {
    return new BezierFunction(geodesicInterface, control);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Tensor control;

  private BezierFunction(GeodesicInterface geodesicInterface, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    this.control = control;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    Clip.unit().requireInside(scalar);
    Tensor points = this.control;
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
