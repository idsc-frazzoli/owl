// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;

public class CurvePoint {
  private final int idx;
  private final Tensor tensor;

  /* package */ CurvePoint(int idx, Tensor tensor) {
    this.idx = idx;
    this.tensor = tensor;
  }

  public int getIndex() {
    return idx;
  }

  public Tensor getTensor() {
    return tensor.unmodifiable();
  }
}
