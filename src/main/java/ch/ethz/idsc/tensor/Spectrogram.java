// code by jph
package ch.ethz.idsc.tensor;

import java.util.function.Function;

import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Abs;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Spectrogram.html">Spectrogram</a> */
// TODO JPH OWL 045 TENSOR 074 obsolete
public enum Spectrogram {
  ;
  /** @param tensor
   * @return */
  public static Tensor of(Tensor tensor) {
    return of(tensor, ColorDataGradients.VISIBLESPECTRUM);
  }

  /** @param tensor
   * @param function
   * @return */
  public static Tensor of(Tensor tensor, Function<Scalar, ? extends Tensor> function) {
    return ArrayPlot.of(array(tensor), function);
  }

  /** @param tensor
   * @return */
  public static Tensor array(Tensor tensor) {
    Tensor spectrogram = SpectrogramArray.of(tensor).map(Abs.FUNCTION);
    int half = Unprotect.dimension1(spectrogram) / 2;
    return Tensors.vector(i -> spectrogram.get(Tensor.ALL, half - i - 1), half);
  }
}
