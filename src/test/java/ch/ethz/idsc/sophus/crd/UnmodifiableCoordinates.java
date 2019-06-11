// code by gjoel
package ch.ethz.idsc.sophus.crd;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class UnmodifiableCoordinates extends Coordinates {
  UnmodifiableCoordinates(Tensor vector, CoordinateSystem system) {
    super(vector.unmodifiable(), system);
  }

  @Override
  public UnmodifiableCoordinates unmodifiable() {
    return this;
  }

  @Override
  public void set(Tensor tensor, Integer... index) {
    throw new UnsupportedOperationException("unmodifiable");
  }
}
