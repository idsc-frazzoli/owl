// code by gjoel
package ch.ethz.idsc.sophus.crd;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class Coordinates implements Tensor {
  public static Coordinates of(Tensor vector) {
    return vector instanceof Coordinates ? (Coordinates) vector : of(vector, CoordinateSystem.DEFAULT);
  }

  public static Coordinates of(Tensor vector, String system) {
    return of(vector, CoordinateSystem.of(system));
  }

  public static Coordinates of(Tensor vector, CoordinateSystem system) {
    return vector instanceof Coordinates ? CompatibleSystemQ.to(system).require(vector) : new Coordinates(vector, system);
  }

  // ---
  private final Tensor values;
  private final CoordinateSystem system;

  protected Coordinates(Tensor vector, CoordinateSystem system) {
    this.values = VectorQ.require(vector);
    this.system = system;
  }

  public Tensor values() {
    return values.unmodifiable();
  }

  public CoordinateSystem system() {
    return system;
  }

  @Override
  public Coordinates unmodifiable() {
    return new UnmodifiableCoordinates(values, system);
  }

  @Override
  public Coordinates copy() {
    return this;
  }

  @Override
  public Tensor get(Integer... index) {
    return values.get(index);
  }

  @Override
  public Scalar Get(Integer... index) {
    return values.Get(index);
  }

  @Override
  public Tensor get(List<Integer> index) {
    return values.get(index);
  }

  @Override
  public void set(Tensor tensor, Integer... index) {
    if (ScalarQ.of(tensor) && index.length == 1)
      values.set(tensor, index[0]);
    else
      throw TensorRuntimeException.of(tensor, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, Integer... index) {
    set(function.apply((T) Get(index)), index);
  }

  @Override
  public Coordinates append(Tensor tensor) {
    throw new UnsupportedOperationException("unmodifiable");
  }

  @Override
  public int length() {
    return values.length();
  }

  @Override
  public Stream<Tensor> stream() {
    return values.stream();
  }

  @Override
  public Stream<Tensor> flatten(int level) {
    if (level == 0)
      return stream();
    throw new IndexOutOfBoundsException();
  }

  @Override
  public Tensor extract(int fromIndex, int toIndex) {
    return values.extract(fromIndex, toIndex);
  }

  @Override
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    return values.block(fromIndex, dimensions);
  }

  @Override
  public Coordinates negate() {
    return new Coordinates(values.negate(), system);
  }

  @Override
  public Coordinates add(Tensor tensor) {
    return doIfAllowed(tensor, values::add);
  }

  @Override
  public Coordinates subtract(Tensor tensor) {
    return doIfAllowed(tensor, values::subtract);
  }

  @Override
  public Coordinates pmul(Tensor tensor) {
    return doIfAllowed(tensor, values::pmul);
  }

  @Override
  public Coordinates dot(Tensor tensor) {
    return doIfAllowed(tensor, values::dot);
  }

  protected Coordinates doIfAllowed(Tensor coords, TensorUnaryOperator function) {
    if (CompatibleSystemQ.to(this).with(coords))
      return new Coordinates(function.apply(((Coordinates) coords).values), system);
    throw TensorRuntimeException.of(coords, this);
  }

  @Override
  public Coordinates multiply(Scalar scalar) {
    return new Coordinates(values.multiply(scalar), system);
  }

  @Override
  public Coordinates divide(Scalar scalar) {
    return new Coordinates(values.divide(scalar), system);
  }

  @Override
  public Coordinates map(Function<Scalar, ? extends Tensor> function) {
    return new Coordinates(values.map(function), system);
  }

  @Override // from Iterable
  public Iterator<Tensor> iterator() {
    return values.iterator();
  }

  @Override // from Object
  public boolean equals(Object obj) {
    return obj instanceof Coordinates && ((Coordinates) obj).values.equals(values) && ((Coordinates) obj).system().equals(system);
  }

  @Override // from Object
  public String toString() {
    return values.toString() + system.toString();
  }

  @Override // from Object
  public int hashCode() {
    return Objects.hash(values, system.name());
  }
}
