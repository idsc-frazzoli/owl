// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** {@link DeCasteljau} */
// TODO document how this class is intended for use
public class BezierCurve {
  private final GeodesicInterface geodesicInterface;

  public BezierCurve(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  public ScalarTensorFunction evaluation(Tensor points) {
    return new DeCasteljau(geodesicInterface, points);
  }

  public Tensor refine(Tensor points, int number) {
    return Subdivide.of(0, 1, number).map(evaluation(points));
  }
}
