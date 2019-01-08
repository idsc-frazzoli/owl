// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.io.File;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ enum StaticHelper {
  ;
  /** swiss trolley plus */
  @SuppressWarnings("unused")
  static void gps() {
    try {
      Tensor rows = Import.of(new File("/home/datahaki/Documents/datasets/swisstrolley/processed/1.csv"));
      Tensor control = Tensors.empty();
      Scalar RAD2DEG = RealScalar.of(180 / Math.PI);
      int index = 0;
      for (Tensor row : rows) {
        Scalar x = row.Get(0).multiply(RAD2DEG);
        Scalar y = row.Get(1).multiply(RAD2DEG);
        // Tensor transform = WGS84toCH1903LV03Plus.transform(x, y);
        // control.append(transform.append(row.Get(2)));
        ++index;
        if (2000 < index)
          break;
      }
      control = Tensor.of(control.stream().map(r -> r.pmul(Tensors.vector(.1, .1, -1))));
      Tensor mean = Mean.of(control);
      mean.set(RealScalar.of(Math.PI / 2), 2);
      control = Tensor.of(control.stream().map(row -> row.subtract(mean)));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
