// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicCenter implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);

  /** @param radius
   * @return weights of kalman */
  private static Tensor splits(Tensor weights) {
    int radius = (weights.length() - 1) / 2;
    Tensor halfmask = Tensors.vector(i -> i == 0 ? weights.Get(i) : weights.Get(i).multiply(TWO), radius);
    Scalar factor = RealScalar.ONE;
    Tensor splits = Tensors.empty();
    for (int index = 0; index < radius; ++index) {
      Scalar lambda = halfmask.Get(index).divide(factor);
      splits.append(lambda);
      factor = factor.multiply(RealScalar.ONE.subtract(lambda));
    }
    return Reverse.of(splits);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Function<Integer, Tensor> function;
  private final List<Tensor> weights = new ArrayList<>();

  public GeodesicCenter(GeodesicInterface geodesicInterface, Function<Integer, Tensor> function) {
    this.geodesicInterface = geodesicInterface;
    this.function = function;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    int radius = (tensor.length() - 1) / 2;
    synchronized (weights) {
      while (weights.size() <= radius)
        weights.add(splits(function.apply(weights.size())));
    }
    Tensor splits = weights.get(radius);
    Tensor pL = tensor.get(0);
    Tensor pR = tensor.get(2 * radius);
    for (int index = 0; index < radius; ++index) {
      int pos = index + 1;
      Scalar scalar = splits.Get(index);
      pL = geodesicInterface.split(pL, tensor.get(pos), scalar);
      pR = geodesicInterface.split(pR, tensor.get(2 * radius - pos), scalar);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }
}
