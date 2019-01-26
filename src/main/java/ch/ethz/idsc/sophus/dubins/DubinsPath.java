// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/** compatible with the use of Quantity:
 * radius and entries in segLength must have the same unit
 * 
 * immutable */
public class DubinsPath implements Serializable {
  public static enum Type {
    LSR(+1, +0, -1, Steer2TurnsDiffSide.INSTANCE), //
    RSL(-1, +0, +1, Steer2TurnsDiffSide.INSTANCE), //
    LSL(+1, +0, +1, Steer2TurnsSameSide.INSTANCE), //
    RSR(-1, +0, -1, Steer2TurnsSameSide.INSTANCE), //
    LRL(+1, -1, +1, Steer3Turns.INSTANCE), //
    RLR(-1, +1, -1, Steer3Turns.INSTANCE), //
    ;
    // ---
    private final Tensor signature;
    private final Tensor signatureAbs;
    private final boolean isFirstTurnRight;
    private final boolean isFirstEqualsLast;
    private final DubinsSteer dubinsSteer;

    private Type(int s0s, int s1s, int s2s, DubinsSteer dubinsSteer) {
      signature = Tensors.vector(s0s, s1s, s2s).unmodifiable();
      signatureAbs = signature.map(Scalar::abs).unmodifiable();
      isFirstTurnRight = s0s == -1;
      isFirstEqualsLast = s0s == s2s;
      this.dubinsSteer = dubinsSteer;
    }

    /** @return true if type is RSL or RSR or RLR */
    public boolean isFirstTurnRight() {
      return isFirstTurnRight;
    }

    /** @return true if type is LSL or RSR or LRL or RLR */
    public boolean isFirstEqualsLast() {
      return isFirstEqualsLast;
    }

    public Tensor signatureAbs() {
      return signatureAbs;
    }

    /* package */ DubinsSteer dubinsSteer() {
      return dubinsSteer;
    }

    /** @param index 0, 1, or 2
     * @param radius positive
     * @return vector with first and second entry unitless.
     * result is multiplied with length of segment */
    /* package */ Tensor tangent(int index, Scalar radius) {
      return Tensors.of(RealScalar.ONE, RealScalar.ZERO, //
          signature.Get(index).divide(Sign.requirePositive(radius)));
    }
  }

  private final Type type;
  private final Scalar radius;
  private final Tensor segLength;
  private final Scalar length;

  /** @param type non-null
   * @param radius positive
   * @param segLength {length1, length2, length3} each non-negative
   * @throws Exception if radius is non-positive */
  public DubinsPath(Type type, Scalar radius, Tensor segLength) {
    this.type = Objects.requireNonNull(type);
    this.radius = Sign.requirePositive(radius);
    this.segLength = VectorQ.requireLength(segLength, 3);
    length = segLength.stream() //
        .map(Scalar.class::cast) //
        .map(Sign::requirePositiveOrZero) //
        .reduce(Scalar::add).get();
  }

  /** @return dubins path type */
  public Type type() {
    return type;
  }

  /** @return vector of length 3 with parameter values of transition points */
  public Tensor segments() {
    return Accumulate.of(segLength);
  }

  /** @return total length of Dubins path in Euclidean space */
  public Scalar length() {
    return length;
  }

  /** @return total curvature, return value is non-negative */
  public Scalar curvature() {
    return segLength.dot(type.signatureAbs()).divide(radius).Get();
  }

  /** parameterization of Dubins path over the closed interval [length().zero(), length()]
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
        Tensor x = type.tangent(index, radius);
        if (Scalars.lessEquals(scalar, segLength.Get(index)))
          return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(scalar));
        g = Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(segLength.Get(index)));
        scalar = scalar.subtract(segLength.Get(index));
      }
      Tensor x = type.tangent(2, radius);
      return Se2CoveringIntegrator.INSTANCE.spin(g, x.multiply(scalar));
    }
  }
}
