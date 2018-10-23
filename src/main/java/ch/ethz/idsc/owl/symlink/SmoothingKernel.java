// code by jph
package ch.ethz.idsc.owl.symlink;

import java.util.function.Function;

import ch.ethz.idsc.owl.subdiv.curve.GeodesicMean;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicMeanFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.win.BartlettWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanHarrisWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanNuttallWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanWindow;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.FlatTopWindow;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;
import ch.ethz.idsc.tensor.sca.win.HammingWindow;
import ch.ethz.idsc.tensor.sca.win.HannWindow;
import ch.ethz.idsc.tensor.sca.win.NuttallWindow;
import ch.ethz.idsc.tensor.sca.win.ParzenWindow;
import ch.ethz.idsc.tensor.sca.win.VectorTotal;
import ch.ethz.idsc.tensor.sca.win.WindowFunction;

/** Filter-Design Window Functions
 * 
 * Quote from Mathematica:
 * set of window functions that are commonly used in the design of finite impulse response
 * (FIR) filters, with additional applications in spectral and spatial analysis.
 * In filter design, windows are typically used to reduce unwanted ripples in the frequency
 * response of a filter.
 * 
 * Wikipedia lists the spectrum for each window
 * https://en.wikipedia.org/wiki/Window_function
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/guide/WindowFunctions.html">WindowFunctions</a> */
public enum SmoothingKernel implements Function<Integer, Tensor> {
  BARTLETT(BartlettWindow.function()), //
  BLACKMAN(BlackmanWindow.function()), //
  BLACKMAN_HARRIS(BlackmanHarrisWindow.function()), //
  BLACKMAN_NUTTALL(BlackmanNuttallWindow.function()), //
  /** Dirichlet window
   * constant mask is used in {@link GeodesicMean} and {@link GeodesicMeanFilter} */
  DIRICHLET(DirichletWindow.function()), //
  FLAT_TOP(FlatTopWindow.function()), //
  /** the Gaussian kernel works well in practice
   * in particular for masks of small support */
  GAUSSIAN(GaussianWindow.function()), //
  /** has nice properties in the frequency domain */
  HAMMING(HammingWindow.function()), //
  HANN(HannWindow.function()), //
  NUTTALL(NuttallWindow.function()), //
  PARZEN(ParzenWindow.function()), //
  TUKEY(ParzenWindow.function()), //
  ;
  private final WindowFunction windowFunction;

  private SmoothingKernel(WindowFunction windowFunction) {
    this.windowFunction = windowFunction;
  }

  @Override
  public Tensor apply(Integer i) {
    if (i == 0) //
      return Tensors.vector(1);
    Tensor vector = windowFunction.isContinuous() //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i + 2) //
            .map(windowFunction) //
            .extract(1, 2 * i + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
            .map(windowFunction);
    // TODO V062 refactor
    return Normalize.of(vector, v -> VectorTotal.FUNCTION.apply(v));
  }
}
