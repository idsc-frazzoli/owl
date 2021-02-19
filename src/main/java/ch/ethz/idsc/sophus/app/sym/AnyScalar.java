// code by jph
package ch.ethz.idsc.sophus.app.sym;

import java.io.Serializable;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.AbsInterface;
import ch.ethz.idsc.tensor.api.ArcTanInterface;
import ch.ethz.idsc.tensor.api.ArgInterface;
import ch.ethz.idsc.tensor.api.ComplexEmbedding;
import ch.ethz.idsc.tensor.api.ConjugateInterface;
import ch.ethz.idsc.tensor.api.ExactScalarQInterface;
import ch.ethz.idsc.tensor.api.ExpInterface;
import ch.ethz.idsc.tensor.api.LogInterface;
import ch.ethz.idsc.tensor.api.MachineNumberQInterface;
import ch.ethz.idsc.tensor.api.RoundingInterface;
import ch.ethz.idsc.tensor.api.TrigonometryInterface;

/** any scalar tracks whether a scalar in a tensor has any effect within a computation */
public final class AnyScalar extends AbstractScalar implements //
    AbsInterface, ArcTanInterface, ArgInterface, ComplexEmbedding, ConjugateInterface, //
    ExactScalarQInterface, ExpInterface, LogInterface, MachineNumberQInterface, RoundingInterface, //
    TrigonometryInterface, Serializable {
  public static final Scalar INSTANCE = new AnyScalar();

  /***************************************************/
  private AnyScalar() {
    // ---
  }

  @Override // from AbstractScalar
  public Scalar multiply(Scalar scalar) {
    return !(scalar instanceof AnyScalar) && Scalars.isZero(scalar) //
        ? scalar
        : this;
  }

  @Override // from AbstractScalar
  public Scalar negate() {
    return this;
  }

  @Override // from AbstractScalar
  public Scalar reciprocal() {
    return this;
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    return this;
  }

  @Override // from Scalar
  public Scalar zero() {
    throw new UnsupportedOperationException();
  }

  @Override // from Scalar
  public Scalar one() {
    throw new UnsupportedOperationException();
  }

  @Override // from Scalar
  public Number number() {
    throw new UnsupportedOperationException();
  }

  /***************************************************/
  @Override // from AbsInterface
  public Scalar abs() {
    return this;
  }

  @Override
  public Scalar absSquared() {
    return this;
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    return this;
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return this;
  }

  @Override // from ConjugateInterface
  public Scalar conjugate() {
    return this;
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return this;
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return this;
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return true;
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return this;
  }

  @Override // from LogInterface
  public Scalar log() {
    return this;
  }

  @Override // from MachineNumberQInterface
  public boolean isMachineNumber() {
    return true;
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return this;
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return this;
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return this;
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return this;
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return this;
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return this;
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return this;
  }

  /***************************************************/
  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "any";
  }
}
