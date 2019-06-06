// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.Fourier;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Mod;

/* package */ class FourierWindow implements TensorUnaryOperator {
  public static TensorUnaryOperator of(int windowDuration, int samplingFrequency) {
    return new FourierWindow(Objects.requireNonNull(windowDuration), Objects.requireNonNull(samplingFrequency));
  }

  private final int windowLength;
  private final int fourierLength;

  /* package */ FourierWindow(int windowDuration, int samplingFrequency) {
    this.windowLength = windowDuration * samplingFrequency;
    this.fourierLength = Mod.function(Integer.highestOneBit(windowLength)).apply(RealScalar.of(windowLength)).equals(RealScalar.ZERO) //
        ? windowLength //
        : Integer.highestOneBit(windowLength) * 2;
  }

  @Override
  public Tensor apply(Tensor control) {
    Tensor spectrogram = Tensors.empty();
    for (int index = 0; index < control.length() - windowLength; ++index) {
      Tensor temp = PadRight.zeros(fourierLength).apply(control.extract(index, index + windowLength));
      spectrogram.append(Fourier.of(temp));
    }
    return spectrogram;
  }
}