// code by jph
package ch.ethz.idsc.owl.data;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Floor;

public class LinearRasterMap<T> extends RasterMap<T> {
  private final Tensor scale;

  public LinearRasterMap(Tensor scale) {
    this.scale = scale;
  }

  @Override
  public Tensor toKey(Tensor tensor) {
    return Floor.of(tensor.pmul(scale));
  }
}
