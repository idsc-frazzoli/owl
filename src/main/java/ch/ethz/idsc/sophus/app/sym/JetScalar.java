// code by jph
package ch.ethz.idsc.sophus.app.sym;

import java.util.OptionalInt;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.RationalScalar;
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
import ch.ethz.idsc.tensor.api.ExpInterface;
import ch.ethz.idsc.tensor.api.LogInterface;
import ch.ethz.idsc.tensor.api.PowerInterface;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.SqrtInterface;
import ch.ethz.idsc.tensor.api.TrigonometryInterface;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Cosh;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sinh;

/** automatic differentiation */
public class JetScalar extends AbstractScalar implements //
    ExpInterface, LogInterface, PowerInterface, SqrtInterface, TrigonometryInterface {
  private static final JetScalar EMPTY = new JetScalar(Tensors.empty());

  /** @param vector {f[x], f'[x], f''[x], ...}
   * @return */
  public static Scalar of(Tensor vector) {
    return new JetScalar(VectorQ.require(vector));
  }

  /** @param value
   * @param n
   * @return {value, 1, 0, 0, ...} */
  public static Scalar of(Scalar value, int n) {
    return new JetScalar(Tensors.vector(i -> i == 0 ? value : (i == 1 ? RealScalar.ONE : value.zero()), n));
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

  private static JetScalar chain(Tensor vector, ScalarUnaryOperator f, ScalarUnaryOperator df) {
    if (Tensors.isEmpty(vector))
      return EMPTY;
    return new JetScalar(Join.of( //
        Tensors.of(f.apply(vector.Get(0))), //
        product(((JetScalar) df.apply(new JetScalar(opF(vector)))).vector, opD(vector))));
  }

  /***************************************************/
  private final Tensor vector;

  private JetScalar(Tensor vector) {
    this.vector = vector;
  }

  public Tensor vector() {
    return vector.unmodifiable();
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof JetScalar) {
      JetScalar audiScalar = (JetScalar) scalar;
      return new JetScalar(product(vector, audiScalar.vector));
    }
    return new JetScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new JetScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new JetScalar(reciprocal(vector));
  }

  @Override // from Scalar
  public Scalar zero() {
    return new JetScalar(vector.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    Tensor result = vector.map(Scalar::zero);
    result.set(Scalar::one, 0);
    return new JetScalar(result);
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    JetScalar audiScalar = (JetScalar) scalar;
    return new JetScalar(vector.add(audiScalar.vector));
  }

  /***************************************************/
  @Override // from ExpInterface
  public Scalar exp() {
    return chain(vector, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return chain(vector, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public JetScalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      int expInt = optionalInt.getAsInt();
      if (0 <= expInt) // TODO exponent == zero!?
        return new JetScalar(power(vector, expInt));
    }
    return chain(vector, Power.function(exponent), //
        scalar -> Power.function(exponent.subtract(RealScalar.ONE)).apply(scalar).multiply(exponent));
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(RationalScalar.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return chain(vector, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return chain(vector, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return chain(vector, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return chain(vector, Sinh.FUNCTION, Cosh.FUNCTION);
  }

  /***************************************************/
  @Override
  public int hashCode() {
    return vector.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JetScalar) {
      JetScalar audiScalar = (JetScalar) object;
      return vector.equals(audiScalar.vector);
    }
    return false;
  }

  @Override
  public String toString() {
    return vector.toString();
  }
}
