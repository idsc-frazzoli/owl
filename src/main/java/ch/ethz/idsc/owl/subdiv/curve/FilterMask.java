// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Power;

public enum FilterMask implements Function<Integer, Tensor> {
  /** Dirichlet window
   * constant mask is used in {@link GeodesicMean} and {@link GeodesicMeanFilter} */
  CONSTANT() {
    @Override
    public Tensor apply(Integer i) {
      int width = 2 * i + 1;
      Scalar weight = RationalScalar.of(1, width);
      return Tensors.vector(k -> weight, width);
    }
  }, //
  /** has nice properties in the frequency domain */
  HAMMING() {
    @Override
    public Tensor apply(Integer i) {
      return i == 0 //
          ? Tensors.vector(1)
          : Normalize.of( //
              Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
                  .map(HammingWindow.FUNCTION),
              Norm._1);
    }
  }, //
  BLACKMAN() {
    @Override
    public Tensor apply(Integer i) {
      return i == 0 //
          ? Tensors.vector(1)
          : Normalize.of( //
              Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
                  .map(BlackmanWindow.FUNCTION),
              Norm._1);
    }
  }, //
  /** close to gaussian filter */
  BINOMIAL() {
    @Override
    public Tensor apply(Integer i) {
      int two_i = 2 * i;
      return Tensors.vector(k -> Binomial.of(two_i, k), two_i + 1).divide(Power.of(2, two_i));
    }
  }, //
  ;
}
