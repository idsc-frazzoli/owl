// code by jph
package ch.ethz.idsc.sophus.app;

import ch.ethz.idsc.sophus.flt.ga.GeodesicMean;
import ch.ethz.idsc.sophus.flt.ga.GeodesicMeanFilter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.BartlettWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanHarrisWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanNuttallWindow;
import ch.ethz.idsc.tensor.sca.win.BlackmanWindow;
import ch.ethz.idsc.tensor.sca.win.DirichletWindow;
import ch.ethz.idsc.tensor.sca.win.FlatTopWindow;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;
import ch.ethz.idsc.tensor.sca.win.HammingWindow;
import ch.ethz.idsc.tensor.sca.win.HannWindow;
import ch.ethz.idsc.tensor.sca.win.LanczosWindow;
import ch.ethz.idsc.tensor.sca.win.NuttallWindow;
import ch.ethz.idsc.tensor.sca.win.ParzenWindow;
import ch.ethz.idsc.tensor.sca.win.TukeyWindow;

/** Filter-Design Window Functions
 * 
 * <p>Quote from Mathematica:
 * set of window functions that are commonly used in the design of finite impulse response
 * (FIR) filters, with additional applications in spectral and spatial analysis.
 * In filter design, windows are typically used to reduce unwanted ripples in the frequency
 * response of a filter.
 * 
 * <p>Wikipedia lists the spectrum for each window
 * https://en.wikipedia.org/wiki/Window_function
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/guide/WindowFunctions.html">WindowFunctions</a> */
public enum SmoothingKernel implements ScalarUnaryOperator {
  /** triangle */
  BARTLETT(BartlettWindow.FUNCTION), //
  BLACKMAN(BlackmanWindow.FUNCTION), //
  BLACKMAN_HARRIS(BlackmanHarrisWindow.FUNCTION), //
  BLACKMAN_NUTTALL(BlackmanNuttallWindow.FUNCTION),
  /** Dirichlet window
   * constant mask is used in {@link GeodesicMean} and {@link GeodesicMeanFilter} */
  DIRICHLET(DirichletWindow.FUNCTION),
  /** flat top kernel may consist of negative values or even values close to zero.
   * In a geodesic average this is likely to result in numerical instabilities. */
  FLAT_TOP(FlatTopWindow.FUNCTION),
  /** the Gaussian kernel works well in practice
   * in particular for masks of small support */
  GAUSSIAN(GaussianWindow.FUNCTION),
  /** has nice properties in the frequency domain */
  HAMMING(HammingWindow.FUNCTION), //
  HANN(HannWindow.FUNCTION), //
  LANCZOS(LanczosWindow.FUNCTION), //
  NUTTALL(NuttallWindow.FUNCTION), //
  PARZEN(ParzenWindow.FUNCTION), //
  TUKEY(TukeyWindow.FUNCTION);

  private final ScalarUnaryOperator scalarUnaryOperator;

  private SmoothingKernel(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override
  public Scalar apply(Scalar x) {
    return scalarUnaryOperator.apply(x);
  }
}
