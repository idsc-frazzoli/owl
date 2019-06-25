// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Rafaello d'Andrea: Signals and Systems lecture:
 * https://www.ethz.ch/content/dam/ethz/special-interest/mavt/dynamic-systems-n-control/idsc-dam/Lectures/Signals-and-Systems/Lectures/Fall2018/Lecture%20Notes%204.pdf
 * page 2: Fourier Spectra & p6: Frequency Response of LTI systems */
public enum TransferFunctionResponse implements TensorUnaryOperator {
  MAGNITUDE(Abs.FUNCTION), //
  FREQUENCY(Arg.FUNCTION), //
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