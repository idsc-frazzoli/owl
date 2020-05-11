// code by jph
package ch.ethz.idsc.tensor.ext;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.PowerInterface;

public class AudiScalar extends AbstractScalar implements PowerInterface {
  public static Scalar of(Tensor vector) {
    return new AudiScalar(VectorQ.require(vector));
  }

  public static Scalar of(Scalar value, int n) {
    return new AudiScalar(Tensors.vector(i -> i == 0 ? value : (i == 1 ? RealScalar.ONE : value.zero()), n));
  }

  /** drop function, promote derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opD(Tensor vector) {
    return Drop.head(vector, 1);
  }

  /** keep function and derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opF(Tensor vector) {
    return Drop.tail(vector, 1);
  }

  private static Tensor product(Tensor f, Tensor g) {
    return Tensors.isEmpty(f) && Tensors.isEmpty(g) //
        ? Tensors.empty()
        : Join.of( //
            Tensors.of(f.Get(0).multiply(g.Get(0))), //
            product(opF(f), opD(g)).add(product(opD(f), opF(g))));
  }

  private static Tensor reciprocal(Tensor g) {
    if (Tensors.isEmpty(g))
      return Tensors.empty();
    Tensor opF = opF(g);
    return Join.of( //
        Tensors.of(g.Get(0).reciprocal()), //
        product(opD(g).negate(), reciprocal(product(opF, opF))));
  }

  private static Tensor power(Tensor vector, int n) {
    return n == 0 //
        ? UnitVector.of(vector.length(), 0)
        : product(power(vector, n - 1), vector);
  }

  /***************************************************/
  private final Tensor vector;

  private AudiScalar(Tensor vector) {
    this.vector = vector;
  }

  public Tensor vector() {
    return vector.unmodifiable();
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof AudiScalar) {
      AudiScalar audiScalar = (AudiScalar) scalar;
      return new AudiScalar(product(vector, audiScalar.vector));
    }
    return new AudiScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new AudiScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new AudiScalar(reciprocal(vector));
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Scalar
  public Scalar zero() {
    return new AudiScalar(vector.map(Scalar::zero));
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    AudiScalar audiScalar = (AudiScalar) scalar;
    return new AudiScalar(vector.add(audiScalar.vector));
  }

  /***************************************************/
  @Override // from PowerInterface
  public AudiScalar power(Scalar exponent) {
    return new AudiScalar(power(vector, Integers.requirePositiveOrZero(Scalars.intValueExact(exponent))));
  }

  /***************************************************/
  @Override
  public int hashCode() {
    return vector.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof AudiScalar) {
      AudiScalar audiScalar = (AudiScalar) object;
      return vector.equals(audiScalar.vector);
    }
    return false;
  }

  @Override
  public String toString() {
    return vector.toString();
  }
}
