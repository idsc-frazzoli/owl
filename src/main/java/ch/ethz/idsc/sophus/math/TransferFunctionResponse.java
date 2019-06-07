// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** TODO OB state reference for terminology and references */
public enum TransferFunctionResponse implements TensorUnaryOperator {
  MAGNITUDE(Abs.FUNCTION), //
  FREQUENCY(scalar -> Imag.FUNCTION.apply(scalar).divide(Real.FUNCTION.apply(scalar))), //
  ;
  private final ScalarUnaryOperator scalarUnaryOperator;

  private TransferFunctionResponse(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return tensor.map(scalarUnaryOperator);
  }
}