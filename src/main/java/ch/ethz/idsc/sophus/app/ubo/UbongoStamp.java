// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class UbongoStamp {
  final Tensor stamp;
  final Tensor rows;
  final Tensor cols;

  public UbongoStamp(Tensor stamp) {
    this.stamp = stamp;
    rows = Total.of(stamp);
    cols = Tensor.of(stamp.stream().map(Total::of));
  }
}
