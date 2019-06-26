// code by ob
package ch.ethz.idsc.sophus.flt.ga;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.math.win.HalfWindowSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicExtrapolate projects a sequence of points to their next (expected) point
 * with each point weighted as provided by an external function. */
public class GeodesicExtrapolation implements TensorUnaryOperator {
  /* package */ static class Splits implements Function<Integer, Tensor>, Serializable {
    private final Function<Integer, Tensor> function;

    private Splits(Function<Integer, Tensor> function) {
      this.function = function;
    }

    @Override
    public Tensor apply(Integer t) {
      return of(function.apply(t));
    }

    /** @param causal affine mask
     * @return Tensor [i1, ..., in, e] with i being interpolatory weights and e the extrapolation weight
     * @throws Exception if mask is not affine */
    /* package */ static Tensor of(Tensor mask) {
      // check for affinity
      AffineQ.require(mask);
      // no extrapolation possible
      if (mask.length() == 1)
        return Tensors.vector(1);
      Tensor splits = Tensors.empty();
      Scalar factor = mask.Get(0);
      // Calculate interpolation splits
      for (int index = 1; index < mask.length() - 1; ++index) {
        factor = factor.add(mask.get(index));
        Scalar lambda = mask.Get(index).divide(factor);
        splits.append(lambda);
      }
      // Calculate extrapolation splits
      Scalar temp = RealScalar.ONE;
      for (int index = 0; index < splits.length(); index++) {
        temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
      }
      splits.append(RealScalar.ONE.add(temp.reciprocal()));
      return splits;
    }
  }

  /** @param splitInterface
   * @param function that maps an extent to a weight mask of length "sequence.length - 2"
   * @return operator that maps a sequence of number of points to their next (expected) point
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(SplitInterface splitInterface, Function<Integer, Tensor> function) {
    return new GeodesicExtrapolation(splitInterface, Objects.requireNonNull(function));
  }

  /** @param splitInterface
   * @param windowFunction
   * @return operator that maps a sequence of number of points to their next (expected) point
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(SplitInterface splitInterface, ScalarUnaryOperator windowFunction) {
    return new GeodesicExtrapolation(splitInterface, HalfWindowSampler.of(windowFunction));
  }

  // ---
  private final SplitInterface splitInterface;
  private final Function<Integer, Tensor> function;

  private GeodesicExtrapolation(SplitInterface splitInterface, Function<Integer, Tensor> function) {
    this.splitInterface = Objects.requireNonNull(splitInterface);
    this.function = MemoFunction.wrap(new Splits(function));
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Tensor splits = function.apply(tensor.length());
    Tensor result = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index)
      result = splitInterface.split(result, tensor.get(index), splits.Get(index - 1));
    return result;
  }
}
