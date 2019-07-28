// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.itp.ArcLengthParameterization;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
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
    private final boolean containsStraight;
    private final DubinsSteer dubinsSteer;

    private Type(int s0s, int s1s, int s2s, DubinsSteer dubinsSteer) {
      signature = Tensors.vector(s0s, s1s, s2s).unmodifiable();
      signatureAbs = signature.map(Scalar::abs).unmodifiable();
      isFirstTurnRight = s0s == -1;
      isFirstEqualsLast = s0s == s2s;
      containsStraight = s1s == 0;
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

    public boolean containsStraight() {
      return containsStraight;
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

  /** @param g start configuration
   * @return arc-length parameterization of dubins path starting at given configuration g
   * over the closed interval [0, 1] */
  public ScalarTensorFunction unit(Tensor g) {
    Tensor tensor = Unprotect.empty(4);
    tensor.append(g);
    for (int index = 0; index < 3; ++index)
      tensor.append(g = Se2CoveringIntegrator.INSTANCE.spin(g, type.tangent(index, radius).multiply(segLength.Get(index))));
    return ArcLengthParameterization.of(segLength, Se2CoveringGeodesic.INSTANCE, tensor);
  }

  /** @param g start configuration
   * @return arc-length parameterization of dubins path starting at given configuration g
   * over the closed interval [length().zero(), length()] */
  public ScalarTensorFunction sampler(Tensor g) {
    ScalarTensorFunction scalarTensorFunction = unit(g);
    return scalar -> scalarTensorFunction.apply(scalar.divide(length));
  }
}
