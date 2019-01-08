// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.filter.GeodesicMean;
import ch.ethz.idsc.sophus.filter.GeodesicMeanFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.VectorTotal;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
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
public enum SmoothingKernel implements IntegerTensorFunction {
  BARTLETT(BartlettWindow.FUNCTION), //
  BLACKMAN(BlackmanWindow.FUNCTION), //
  BLACKMAN_HARRIS(BlackmanHarrisWindow.FUNCTION), //
  BLACKMAN_NUTTALL(BlackmanNuttallWindow.FUNCTION), //
  /** Dirichlet window
   * constant mask is used in {@link GeodesicMean} and {@link GeodesicMeanFilter} */
  DIRICHLET(DirichletWindow.FUNCTION), //
  /** flat top kernel may consist of negative values or even values close to zero.
   * In a geodesic average this is likely to result in numerical instabilities. */
  FLAT_TOP(FlatTopWindow.FUNCTION), //
  /** the Gaussian kernel works well in practice
   * in particular for masks of small support */
  GAUSSIAN(GaussianWindow.FUNCTION), //
  /** has nice properties in the frequency domain */
  HAMMING(HammingWindow.FUNCTION), //
  HANN(HannWindow.FUNCTION), //
  NUTTALL(NuttallWindow.FUNCTION), //
  PARZEN(ParzenWindow.FUNCTION), //
  TUKEY(TukeyWindow.FUNCTION), //
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorTotal.FUNCTION);
  // ---
  private final ScalarUnaryOperator scalarUnaryOperator;
  private final boolean isContinuous;

  private SmoothingKernel(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
    isContinuous = Chop._03.allZero(scalarUnaryOperator.apply(RationalScalar.HALF));
  }

  public ScalarUnaryOperator windowFunction() {
    return scalarUnaryOperator;
  }

  @Override
  public Tensor apply(Integer i) {
    if (i == 0)
      return Tensors.vector(1);
    Tensor vector = isContinuous //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i + 2) //
            .map(scalarUnaryOperator) //
            .extract(1, 2 * i + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
            .map(scalarUnaryOperator);
    return NORMALIZE.apply(vector);
  }
}
