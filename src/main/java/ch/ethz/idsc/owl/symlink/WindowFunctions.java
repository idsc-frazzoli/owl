// code by jph
package ch.ethz.idsc.owl.symlink;

import java.util.function.Function;

import ch.ethz.idsc.owl.subdiv.curve.GeodesicMean;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicMeanFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sig.BlackmanWindow;
import ch.ethz.idsc.tensor.sig.DirichletWindow;
import ch.ethz.idsc.tensor.sig.GaussianWindow;
import ch.ethz.idsc.tensor.sig.HammingWindow;
import ch.ethz.idsc.tensor.sig.HannWindow;
import ch.ethz.idsc.tensor.sig.NuttallWindow;
import ch.ethz.idsc.tensor.sig.ParzenWindow;
import ch.ethz.idsc.tensor.sig.TukeyWindow;
import ch.ethz.idsc.tensor.sig.VectorTotal;

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
public enum WindowFunctions implements Function<Integer, Tensor> {
  /** close to gaussian filter */
  BINOMIAL(s -> s) {
    @Override
    public Tensor apply(Integer i) {
      if (i < 0)
        throw new IllegalArgumentException("i=" + i);
      int two_i = 2 * i;
      return Tensors.vector(k -> Binomial.of(two_i, k), two_i + 1).divide(Power.of(2, two_i));
    }
  }, //
  BLACKMAN(BlackmanWindow.FUNCTION), //
  /** Dirichlet window
   * constant mask is used in {@link GeodesicMean} and {@link GeodesicMeanFilter} */
  DIRICHLET(DirichletWindow.FUNCTION), //
  GAUSSIAN(GaussianWindow.FUNCTION), //
  /** has nice properties in the frequency domain */
  HAMMING(HammingWindow.FUNCTION), //
  HANN(HannWindow.FUNCTION), //
  NUTTALL(NuttallWindow.FUNCTION), //
  PARZEN(ParzenWindow.FUNCTION), //
  TUKEY(TukeyWindow.FUNCTION), //
  ;
  private final ScalarUnaryOperator scalarUnaryOperator;
  private final boolean isZero;

  private WindowFunctions(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
    isZero = Chop._10.allZero(scalarUnaryOperator.apply(RationalScalar.HALF));
  }

  /** @return true if function at 1/2 evaluates to zero */
  boolean isZero() {
    return isZero;
  }

  @Override
  public Tensor apply(Integer i) {
    if (i == 0) //
      return Tensors.vector(1);
    Tensor vector = isZero //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i + 2) //
            .map(scalarUnaryOperator) //
            .extract(1, 2 * i + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
            .map(scalarUnaryOperator);
    // TODO V062 refactor
    return Normalize.of(vector, v -> VectorTotal.FUNCTION.apply(v));
  }
}
