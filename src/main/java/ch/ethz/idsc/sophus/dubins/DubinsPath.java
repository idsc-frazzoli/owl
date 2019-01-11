// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/** compatible with the use of Quantity:
 * radius and entries in segLength must have the same unit
 * 
 * immutable */
public class DubinsPath {
  private final DubinsPathType dubinsPathType;
  private final Scalar radius;
  private final Tensor segLength;
  private final Scalar length;

  /** @param dubinsPathType non-null
   * @param radius positive
   * @param segLength {length1, length2, length3} each non-negative
   * @throws Exception if radius is non-positive */
  public DubinsPath(DubinsPathType dubinsPathType, Scalar radius, Tensor segLength) {
    this.dubinsPathType = Objects.requireNonNull(dubinsPathType);
    this.radius = Sign.requirePositive(radius);
    this.segLength = VectorQ.requireLength(segLength, 3);
    length = segLength.stream() //
        .map(Scalar.class::cast) //
        .map(Sign::requirePositiveOrZero) //
        .reduce(Scalar::add).get();
  }

  /** @return dubins path type */
  public DubinsPathType dubinsPathType() {
    return dubinsPathType;
  }

  /** @return vector of length 3 with parameter values of transition points */
  public Tensor segments() {
    return Accumulate.of(segLength);
  }

  /** @return total length of Dubins path in Euclidean space */
  public Scalar length() {
    return length;
  }

  /** @return total curvature */
  public Scalar curvature() {
    return segLength.dot(dubinsPathType.signatureAbs()).Get().divide(radius);
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

    /** parameter scalar is of same unit as length() of dubins path */
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
