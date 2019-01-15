// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.io.File;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ enum StaticHelper {
  ;
  private static final Scalar DEG2RAD = Degree.of(1);

  /** swiss trolley plus */
  @SuppressWarnings("unused")
  static void gps() {
    try {
      Tensor rows = Import.of(new File("/home/datahaki/Documents/datasets/swisstrolley/processed/1.csv"));
      Tensor control = Tensors.empty();
      int index = 0;
      for (Tensor row : rows) {
        Scalar x = row.Get(0).divide(DEG2RAD);
        Scalar y = row.Get(1).divide(DEG2RAD);
        // Tensor transform = WGS84toCH1903LV03Plus.transform(x, y);
        // control.append(transform.append(row.Get(2)));
        ++index;
        if (2000 < index)
          break;
      }
      control = Tensor.of(control.stream().map(r -> r.pmul(Tensors.vector(0.1, 0.1, -1))));
      Tensor mean = Mean.of(control);
      mean.set(Pi.HALF, 2);
      control = Tensor.of(control.stream().map(row -> row.subtract(mean)));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
