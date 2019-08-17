// code by jph
package ch.ethz.idsc.sophus.sym;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ExactScalarQInterface;
import ch.ethz.idsc.tensor.sca.MachineNumberQInterface;
import ch.ethz.idsc.tensor.sca.RoundingInterface;
import ch.ethz.idsc.tensor.sca.TrigonometryInterface;

/** any scalar tracks whether a scalar in a tensor has any effect within a computation */
public final class AnyScalar extends ScalarAdapter implements //
    ExactScalarQInterface, MachineNumberQInterface, RoundingInterface, TrigonometryInterface, Serializable {
  public static final Scalar INSTANCE = new AnyScalar();

  // ---
  private AnyScalar() {
    // ---
  }

  @Override // from ScalarAdapter
  public Scalar multiply(Scalar scalar) {
    return this;
  }

  @Override // from ScalarAdapter
  public Scalar negate() {
    return this;
  }

  @Override // from ScalarAdapter
  public Scalar reciprocal() {
    return this;
  }

  @Override // from ScalarAdapter
  public Scalar abs() {
    return this;
  }

  @Override // from ScalarAdapter
  protected Scalar plus(Scalar scalar) {
    return this;
  }

  /***************************************************/
  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return true;
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
  public String toString() {
    return "any";
  }
}
