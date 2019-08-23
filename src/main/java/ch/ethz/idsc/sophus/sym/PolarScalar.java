// code by jph
package ch.ethz.idsc.sophus.sym;

import java.util.Objects;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.ArgInterface;
import ch.ethz.idsc.tensor.sca.ComplexEmbedding;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;

public class PolarScalar extends AbstractScalar implements //
    ArgInterface, ComplexEmbedding {
  public static PolarScalar of(Scalar abs, Scalar arg) {
    return new PolarScalar( //
        Sign.requirePositiveOrZero(abs), //
        arg);
  }

  public static PolarScalar unit(Scalar arg) {
    return new PolarScalar( //
        RealScalar.ONE, //
        arg);
  }

  // ---
  private final Scalar abs;
  private final Scalar arg;

  private PolarScalar(Scalar abs, Scalar arg) {
    this.abs = abs;
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
    return new PolarScalar( //
        abs.negate(), //
        arg);
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
    if (scalar instanceof PolarScalar) {
      Scalar p_real = real();
      Scalar p_imag = imag();
      PolarScalar polarScalar = (PolarScalar) scalar;
      Scalar q_real = polarScalar.real();
      Scalar q_imag = polarScalar.imag();
      Scalar r_real = p_real.add(q_real);
      Scalar r_imag = p_imag.add(q_imag);
      return new PolarScalar( //
          Hypot.of(r_real, r_imag), //
          ArcTan.of(r_real, r_imag));
    }
    throw TensorRuntimeException.of(this);
  }

  /***************************************************/
  @Override // from ArgInterface
  public Scalar arg() {
    return arg;
  }

  @Override
  public PolarScalar conjugate() {
    return new PolarScalar( //
        abs, //
        arg.negate());
  }

  @Override
  public Scalar real() {
    return abs.multiply(Cos.FUNCTION.apply(arg));
  }

  @Override
  public Scalar imag() {
    return abs.multiply(Sin.FUNCTION.apply(arg));
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
