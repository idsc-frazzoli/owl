// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sig.BlackmanWindow;
import ch.ethz.idsc.tensor.sig.DirichletWindow;
import ch.ethz.idsc.tensor.sig.GaussianWindow;
import ch.ethz.idsc.tensor.sig.HammingWindow;
import ch.ethz.idsc.tensor.sig.HannWindow;
import ch.ethz.idsc.tensor.sig.NuttallWindow;
import ch.ethz.idsc.tensor.sig.TukeyWindow;

public enum FilterMask implements Function<Integer, Tensor> {
  /** close to gaussian filter */
  BINOMIAL(null) {
    @Override
    public Tensor apply(Integer i) {
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
  TUKEY(TukeyWindow.FUNCTION), //
  ;
  private final ScalarUnaryOperator scalarUnaryOperator;

  private FilterMask(ScalarUnaryOperator scalarUnaryOperator) {
    this.scalarUnaryOperator = scalarUnaryOperator;
  }

  @Override
  public Tensor apply(Integer i) {
    return i == 0 //
        ? Tensors.vector(1)
        : Normalize.of( //
            Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i).map(scalarUnaryOperator), //
            Norm._1);
  }
}
