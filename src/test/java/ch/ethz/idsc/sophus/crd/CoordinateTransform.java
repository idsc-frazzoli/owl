// code by gjoel
package ch.ethz.idsc.sophus.crd;

import java.util.function.Function;

import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH remove if not needed
/* package */ abstract class CoordinateTransform implements Function<Coordinates, Coordinates> {
  protected final TensorUnaryOperator tensorUnaryOperator;
  protected final CoordinateSystem from;
  protected final CoordinateSystem to;

  protected CoordinateTransform(TensorUnaryOperator tensorUnaryOperator, CoordinateSystem from, CoordinateSystem to) {
    this.tensorUnaryOperator = tensorUnaryOperator;
    this.from = from;
    this.to = to;
  }

  public CoordinateSystem from() {
    return from;
  }

  public CoordinateSystem to() {
    return to;
  }

  public CoordinateTransform inverse() {
    return new CoordinateTransform(inverseTensorUnaryOperator(), to, from) {
      @Override
      protected TensorUnaryOperator inverseTensorUnaryOperator() {
        return tensorUnaryOperator;
      }
    };
  }

  public CoordinateTransform leftMultiply(CoordinateTransform transform) {
    return transform.rightMultiply(this);
  }

  public CoordinateTransform rightMultiply(CoordinateTransform transform) {
    CompatibleSystemQ.to(to).require(transform.from);
    TensorUnaryOperator operator = coords -> transform.tensorUnaryOperator.apply(tensorUnaryOperator.apply(coords));
    return new CoordinateTransform(operator, from, transform.to) {
      @Override
      protected TensorUnaryOperator inverseTensorUnaryOperator() {
        return coords -> inverse().apply(transform.inverse().apply(Coordinates.of(coords, from)));
      }
    };
  }

  @Override // from TensorUnaryOperator
  public Coordinates apply(Coordinates coords) {
    return Coordinates.of(tensorUnaryOperator.apply(CompatibleSystemQ.to(from).require(coords).values()), to);
  }

  protected abstract TensorUnaryOperator inverseTensorUnaryOperator();
}
