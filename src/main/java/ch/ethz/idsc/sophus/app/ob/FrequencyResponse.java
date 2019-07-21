// code by ob
package ch.ethz.idsc.sophus.app.ob;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference 1:
 * "Frequency response is a measure of magnitude and phase of the
 * output as a function of frequency, in comparison to the input."
 * https://en.wikipedia.org/wiki/Frequency_response
 * 
 * Reference 2:
 * Rafaello d'Andrea: Signals and Systems lecture:
 * https://www.ethz.ch/content/dam/ethz/special-interest/mavt/dynamic-systems-n-control/idsc-dam/Lectures/Signals-and-Systems/Lectures/Fall2018/Lecture%20Notes%204.pdf
 * page 2: Fourier Spectra & p6: Frequency Response of LTI systems
 * 
 * "H=Y/U is the frequency response of the system" */
/* package */ enum FrequencyResponse implements TensorUnaryOperator {
  MAGNITUDE(Scalar::abs), //
  PHASE(Arg.FUNCTION), //
  ;
  private final ScalarUnaryOperator scalarUnaryOperator;

  private FrequencyResponse(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return tensor.map(scalarUnaryOperator);
  }
}