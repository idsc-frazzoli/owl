// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.SymmetricVectorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicCenterMidSeeded projects a sequence of points to their geodesic center
 * Difference to GeodesicCenter: starting to average in the center of the tree going outwards
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicCenterMidSeeded implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);

  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    return new GeodesicCenterMidSeeded(geodesicInterface, Objects.requireNonNull(function));
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, ScalarUnaryOperator windowFunction) {
    return new GeodesicCenterMidSeeded(geodesicInterface, new WindowCenterSampler(windowFunction));
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final IntegerTensorFunction function;
  private final List<Tensor> weights = new ArrayList<>();

  private GeodesicCenterMidSeeded(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    // spatial neigborhood we want to consider for centering
    int radius = (tensor.length() - 1) / 2;
    synchronized (weights) {
      while (weights.size() <= radius)
        weights.add(splits(function.apply(weights.size())));
    }
    Tensor splits = weights.get(radius);
    Tensor pL = tensor.get(radius);
    Tensor pR = tensor.get(radius);
    for (int index = 0; index < radius;) {
      Scalar scalar = splits.Get(index++);
      pL = geodesicInterface.split(tensor.get(radius - index), pL, RealScalar.ONE.subtract(scalar));
      pR = geodesicInterface.split(pR, tensor.get(radius + index), scalar);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }

  /** @param mask symmetric vector of odd length
   * @return weights of Kalman-style iterative moving average
   * @throws Exception if mask is not symmetric or has even number of elements */
  /* package */ static Tensor splits(Tensor mask) {
    if (mask.length() % 2 == 0)
      throw TensorRuntimeException.of(mask);
    SymmetricVectorQ.require(mask);
    int radius = (mask.length() - 1) / 2;
    Tensor halfmask = Tensors.vector(i -> i == 0 //
        ? mask.Get(radius + i).divide(TWO)
        : mask.Get(radius + i), radius + 1);
    Scalar factor = halfmask.Get(0);
    Tensor splits = Tensors.empty();
    for (int index = 1; index <= radius; ++index) {
      Scalar lambda = factor.divide(factor.add(halfmask.Get(index)));
      factor = factor.add(halfmask.Get(index));
      splits.append(lambda);
    }
    return splits;
  }
}
