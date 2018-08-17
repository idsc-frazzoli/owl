// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.owl.math.map.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;

public class DubinsPath {
  public final DubinsPathType dubinsPathType;
  public final Scalar radius;
  private final Tensor segLength;

  public DubinsPath(DubinsPathType dubinsPathType, Scalar radius, Tensor segLength) {
    this.dubinsPathType = dubinsPathType;
    this.radius = radius;
    this.segLength = segLength;
  }

  public Scalar length() {
    return (Scalar) Total.of(segLength);
  }

  public Tensor getPoseAt(Tensor start, Scalar lambda) {
    Tensor g = start;
    for (int index = 0; index < 3; ++index) {
      Tensor x = dubinsPathType.tangent(index, radius);
      if (Scalars.lessEquals(lambda, segLength.Get(index)))
        return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(lambda));
      g = Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(segLength.Get(index)));
      lambda = lambda.subtract(segLength.Get(index));
    }
    return g;
  }
}
