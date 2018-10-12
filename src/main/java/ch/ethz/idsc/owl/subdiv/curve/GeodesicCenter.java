// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Careful: the implementation only supports sequences with odd number of elements
 * 
 * projects a sequence of points to their geodesic center
 * with each point weighted as provided by an external function */
// TODO class is not yet serializable
public class GeodesicCenter implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function
   * @return
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, Function<Integer, Tensor> function) {
    return new GeodesicCenter(geodesicInterface, function);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Function<Integer, Tensor> function;
  private final List<Tensor> weights = new ArrayList<>();

  private GeodesicCenter(GeodesicInterface geodesicInterface, Function<Integer, Tensor> function) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.function = Objects.requireNonNull(function);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    // TODO support sequences with even number of elements
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
