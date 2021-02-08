// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

public class CurvePoint implements Serializable {
  private final int index;
  private final Tensor tensor;

  /* package */ CurvePoint(int index, Tensor tensor) {
    this.index = index;
    this.tensor = tensor;
  }

  public int getIndex() {
    return index;
  }

  public Tensor getTensor() {
    return tensor.unmodifiable();
  }

  public CurvePoint withIndex(int index) {
    return new CurvePoint(index, tensor);
  }
}
