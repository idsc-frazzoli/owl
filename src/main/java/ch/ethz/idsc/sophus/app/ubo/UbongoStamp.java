// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class UbongoStamp implements Serializable {
  final Tensor stamp;
  final Tensor rows;
  final Tensor cols;

  public UbongoStamp(Tensor stamp) {
    this.stamp = stamp;
    rows = Total.of(stamp);
    cols = Tensor.of(stamp.stream().map(Total::of));
  }
}
