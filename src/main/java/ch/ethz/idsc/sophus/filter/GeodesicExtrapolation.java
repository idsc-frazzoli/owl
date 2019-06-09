// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicExtrapolate projects a sequence of points to their next (expected) point
 * with each point weighted as provided by an external function. */
public class GeodesicExtrapolation implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length "sequence.length - 2"
   * @return operator that maps a sequence of number of points to their next (expected) point
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    return new GeodesicExtrapolation(geodesicInterface, Objects.requireNonNull(function));
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return operator that maps a sequence of number of points to their next (expected) point
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, ScalarUnaryOperator windowFunction) {
    return new GeodesicExtrapolation(geodesicInterface, WindowSideSampler.of(windowFunction));
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Function<Integer, Tensor> function;
  private final List<Tensor> weights = new ArrayList<>();

  private GeodesicExtrapolation(GeodesicInterface geodesicInterface, Function<Integer, Tensor> function) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int radius = (tensor.length() - 1);
    synchronized (weights) {
      while (weights.size() <= radius)
        weights.add(splits(function.apply(weights.size())));
    }
    Tensor splits = weights.get(radius);
    Tensor result = tensor.get(0);
    for (int index = 0; index < radius; ++index)
      result = geodesicInterface.split(result, tensor.get(index + 1), splits.Get(index));
    return result;
  }

  /** @param causal affine mask
   * @return Tensor [i1, ..., in, e] with i being interpolatory weights and e the extrapolation weight
   * @throws Exception if mask is not affine */
  /* package */ static Tensor splits(Tensor mask) {
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
