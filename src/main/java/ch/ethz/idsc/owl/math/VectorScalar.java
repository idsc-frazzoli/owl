// code by ynager
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.math.MathContext;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ChopInterface;
import ch.ethz.idsc.tensor.api.ComplexEmbedding;
import ch.ethz.idsc.tensor.api.ConjugateInterface;
import ch.ethz.idsc.tensor.api.ExactScalarQInterface;
import ch.ethz.idsc.tensor.api.NInterface;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Conjugate;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Real;

/** immutable as required by {@link Scalar} interface
 * 
 * <p>multiplication of two {@link VectorScalar}s throws an exception.
 * Careful: multiplication with {@link Quantity} is not commutative !
 * 
 * <p>Comparable<Scalar> is defined only between two instances of {@link VectorScalar}.
 * The lexicographical ordering is applied to the tensors.
 * For instance: [1, 2, 0] < [1, 1, 3]
 * 
 * <p>The sign of a {@link VectorScalar} is not defined. For instance,
 * to check that all entries are non-negative use
 * Scalars.lessEquals(vectorScalar.zero(), vectorScalar);
 * 
 * <p>The string expression of a {@link VectorScalar} is of the form [1, 2, 3]
 * to prevent confusion with standard vectors that are formatted as {1, 2, 3}.
 * 
 * <p>Hint: a {@link VectorScalar} may also have zero entries */
// API not finalized: should VectorScalar allow entries with other VectorScalar's?
public class VectorScalar extends AbstractScalar implements //
    ChopInterface, ComplexEmbedding, ConjugateInterface, ExactScalarQInterface, //
    NInterface, Comparable<Scalar>, Serializable {
  /** @param vector
   * @return
   * @throws Exception if input is not a vector, or contains entries of type {@link VectorScalar} */
  public static Scalar of(Tensor vector) {
    if (vector.stream().map(Scalar.class::cast).anyMatch(VectorScalar.class::isInstance))
      throw TensorRuntimeException.of(vector);
    return new VectorScalar(vector.copy());
  }

  /** @param numbers
   * @return */
  public static Scalar of(Number... numbers) {
    return new VectorScalar(Tensors.vector(numbers));
  }

  public static Scalar of(Stream<Scalar> stream) {
    return new VectorScalar(Tensor.of(stream));
  }

  /***************************************************/
  private final Tensor vector;

  private VectorScalar(Tensor vector) {
    this.vector = vector;
  }

  /** @return unmodifiable content vector */
  public Tensor vector() {
    return vector.unmodifiable();
  }

  /** @param index
   * @return */
  public Scalar at(int index) {
    return vector.Get(index);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof VectorScalar)
      throw TensorRuntimeException.of(this, scalar);
    return new VectorScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new VectorScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Scalar
  public Scalar zero() {
    return new VectorScalar(vector.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  /***************************************************/
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof VectorScalar) {
      VectorScalar vectorScalar = (VectorScalar) scalar;
      return new VectorScalar(vector.add(vectorScalar.vector));
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  /***************************************************/
  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return new VectorScalar(chop.of(vector));
  }

  @Override // from ComplexEmbedding
  public Scalar conjugate() {
    return new VectorScalar(Conjugate.of(vector));
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return new VectorScalar(Real.of(vector));
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return new VectorScalar(Imag.of(vector));
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return ExactTensorQ.of(vector);
  }

  @Override // from NInterface
  public Scalar n() {
    return new VectorScalar(N.DOUBLE.of(vector));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    return new VectorScalar(N.in(mathContext.getPrecision()).of(vector));
  }

  /***************************************************/
  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof VectorScalar) {
      VectorScalar vectorScalar = (VectorScalar) scalar;
      return VectorLexicographic.COMPARATOR.compare(vector, vectorScalar.vector());
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from Scalar
  public int hashCode() {
    return vector.hashCode();
  }

  @Override // from Scalar
  public boolean equals(Object object) {
    return object instanceof VectorScalar && vector.equals(((VectorScalar) object).vector);
  }

  private static final Collector<CharSequence, ?, String> EMBRACE = Collectors.joining(", ", "[", "]");

  @Override // from Scalar
  public String toString() {
    return vector.stream().map(Tensor::toString).collect(EMBRACE);
  }
}
