// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.owl.math.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;

/** immutable */
public class DubinsPath {
  private final DubinsPathType dubinsPathType;
  private final Scalar radius;
  private final Tensor segLength;

  public DubinsPath(DubinsPathType dubinsPathType, Scalar radius, Tensor segLength) {
    this.dubinsPathType = dubinsPathType;
    this.radius = radius;
    this.segLength = segLength;
  }

  /** @return total length of Dubins path in Euclidean space */
  public Scalar length() {
    return (Scalar) Total.of(segLength);
  }

  /** @param g start configuration
   * @return scalar function for input in the interval [0, length()] */
  public ScalarTensorFunction sampler(Tensor g) {
    return new AbsoluteDubinsPath(g);
  }

  private class AbsoluteDubinsPath implements ScalarTensorFunction {
    private final Tensor g;

    AbsoluteDubinsPath(Tensor g) {
      this.g = g;
    }

    @Override // from ScalarTensorFunction
    public Tensor apply(Scalar scalar) {
      Tensor g = this.g;
      for (int index = 0; index < 3; ++index) {
        Tensor x = dubinsPathType.tangent(index, radius);
        if (Scalars.lessEquals(scalar, segLength.Get(index)))
          return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(scalar));
        g = Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(segLength.Get(index)));
        scalar = scalar.subtract(segLength.Get(index));
      }
      return g;
    }
  }
}
