// code by ob
package ch.ethz.idsc.sophus.math;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.Fourier;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class FourierWindow implements TensorUnaryOperator {
  public static TensorUnaryOperator of(int windowDuration, int samplingFrequency) {
    return new FourierWindow(Objects.requireNonNull(windowDuration), Objects.requireNonNull(samplingFrequency));
  }

  // ---
  private final int windowLength;
  private final TensorUnaryOperator tensorUnaryOperator;

  private FourierWindow(int windowDuration, int samplingFrequency) {
    windowLength = windowDuration * samplingFrequency;
    int highestOneBit = Integer.highestOneBit(windowLength);
    tensorUnaryOperator = windowLength == highestOneBit //
        ? t -> t //
        : PadRight.zeros(highestOneBit * 2);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor signal) {
    int total = signal.length() - windowLength;
    Tensor spectrogram = Unprotect.empty(total);
    for (int index = 0; index < total; ++index) {
      Tensor temp = tensorUnaryOperator.apply(signal.extract(index, index + windowLength));
      spectrogram.append(Fourier.of(temp));
    }
    return spectrogram;
  }
}