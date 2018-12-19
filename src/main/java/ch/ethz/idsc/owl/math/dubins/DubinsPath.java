// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clip;

/** compatible with the use of Quantity:
 * radius and entries in segLength must have the same unit
 * 
 * immutable */
public class DubinsPath {
  private final DubinsPathType dubinsPathType;
  private final Scalar radius;
  private final Tensor segLength;

  /** @param dubinsPathType
   * @param radius
   * @param segLength {length1, length2, length3} */
  public DubinsPath(DubinsPathType dubinsPathType, Scalar radius, Tensor segLength) {
    this.dubinsPathType = dubinsPathType;
    this.radius = radius;
    this.segLength = segLength;
  }

  /** @return total length of Dubins path in Euclidean space */
  public Scalar length() {
    return (Scalar) Total.of(segLength);
  }

  /** parameterization of dubins path over the closed interval [length().zero(), length()]
   * 
   * @param g start configuration
   * @return scalar function for input in the interval [0, length()] */
  public ScalarTensorFunction sampler(Tensor g) {
    return new AbsoluteDubinsPath(g);
  }

  private class AbsoluteDubinsPath implements ScalarTensorFunction {
    private final Tensor g;
    private final Clip clip;

    AbsoluteDubinsPath(Tensor g) {
      this.g = g;
      Scalar length = length();
      clip = Clip.function(length.zero(), length);
    }

    @Override // from ScalarTensorFunction
    public Tensor apply(Scalar scalar) {
      Tensor g = this.g;
      clip.requireInside(scalar);
      for (int index = 0; index < 2; ++index) {
        Tensor x = dubinsPathType.tangent(index, radius);
        if (Scalars.lessEquals(scalar, segLength.Get(index)))
          return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(scalar));
        g = Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(segLength.Get(index)));
        scalar = scalar.subtract(segLength.Get(index));
      }
      Tensor x = dubinsPathType.tangent(2, radius);
      return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(scalar));
    }
  }
}
