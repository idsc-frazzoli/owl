// code by jph
package ch.ethz.idsc.sophus.app.ext;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;

public enum MakeAffine {
  ;
  public static Tensor of(Tensor tensor) {
    return tensor.copy().append(RealScalar.ONE.subtract(Total.ofVector(tensor)));
  }
}
