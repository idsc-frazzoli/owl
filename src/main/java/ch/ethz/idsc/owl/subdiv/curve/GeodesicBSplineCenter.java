// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class GeodesicBSplineCenter implements TensorUnaryOperator {
  private final GeodesicInterface geodesicInterface;
  private final List<Tensor> weights = new ArrayList<>();

  public GeodesicBSplineCenter(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    // TODO support sequences with even number of elements
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    int radius = (tensor.length() - 1) / 2;
    synchronized (weights) {
      while (weights.size() <= radius)
        weights.add(StaticHelper.splits(BSplineLimitMask.FUNCTION.apply(tensor.length())));
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
