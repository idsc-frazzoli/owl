// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** GeodesicCenter projects a sequence of points to their geodesic center
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicCenter implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function
   * @return
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    return new GeodesicCenter(geodesicInterface, function);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final IntegerTensorFunction function;
  private final List<Tensor> weights = new ArrayList<>();

  private GeodesicCenter(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.function = Objects.requireNonNull(function);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    int radius = (tensor.length() - 1) / 2;
    synchronized (weights) {
      while (weights.size() <= radius)
        weights.add(StaticHelper.splits(function.apply(weights.size())));
    }
    Tensor splits = weights.get(radius);
    Tensor pL = tensor.get(0);
    Tensor pR = tensor.get(2 * radius);
    for (int index = 0; index < radius;) {
      Scalar scalar = splits.Get(index++);
      pL = geodesicInterface.split(pL, tensor.get(index), scalar);
      pR = geodesicInterface.split(pR, tensor.get(2 * radius - index), scalar);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }
}
