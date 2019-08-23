// code by jph
package ch.ethz.idsc.sophus.sym;

import java.util.Objects;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.ArgInterface;
import ch.ethz.idsc.tensor.sca.Sign;

public class PolarScalar extends AbstractScalar implements ArgInterface {
  public static PolarScalar of(Scalar abs, Scalar arg) {
    return new PolarScalar( //
        Sign.requirePositiveOrZero(abs), //
        arg);
  }

  // ---
  private final Scalar abs;
  private final Scalar arg;

  private PolarScalar(Scalar abs, Scalar arg) {
    this.abs = Sign.requirePositiveOrZero(abs);
    this.arg = arg;
  }

  /***************************************************/
  @Override
  public PolarScalar multiply(Scalar scalar) {
    if (scalar instanceof PolarScalar) {
      PolarScalar polarScalar = (PolarScalar) scalar;
      return new PolarScalar( //
          abs.multiply(polarScalar.abs), //
          arg.add(polarScalar.arg));
    }
    if (scalar instanceof RealScalar)
      return new PolarScalar( //
          abs.multiply(scalar), //
          arg);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public PolarScalar negate() {
    throw TensorRuntimeException.of(this);
  }

  @Override
  public PolarScalar reciprocal() {
    return new PolarScalar( //
        abs.reciprocal(), //
        arg.negate());
  }

  @Override
  public Scalar abs() {
    return abs;
  }

  @Override
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override
  public PolarScalar zero() {
    return new PolarScalar( //
        abs.zero(), //
        arg.zero());
  }

  @Override
  protected PolarScalar plus(Scalar scalar) {
    throw TensorRuntimeException.of(this);
  }

  /***************************************************/
  @Override
  public Scalar arg() {
    return arg;
  }

  /***************************************************/
  @Override
  public int hashCode() {
    return Objects.hash(abs, arg);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof PolarScalar) {
      PolarScalar polarScalar = (PolarScalar) object;
      return abs.equals(polarScalar.abs) //
          && arg.equals(polarScalar.arg);
    }
    return false;
  }

  @Override
  public String toString() {
    return "{\"abs\": " + abs + ", \"arg\": " + arg + "}";
  }
}
