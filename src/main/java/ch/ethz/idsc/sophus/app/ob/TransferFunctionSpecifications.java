// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

public enum TransferFunctionSpecifications {
  FUNCTION;
  public static Tensor MagnitudeResponse(Tensor transferFunction) {
    return Tensor.of(transferFunction.get(0).stream().map(h -> Abs.FUNCTION.apply((Scalar) h)));
  }

  public static Tensor FrequencyRespone(Tensor transferFunction) {
    return Tensor.of(transferFunction.get(0).stream().map(h -> Imag.of(h).divide(Real.of((Scalar) h))));
  }
}