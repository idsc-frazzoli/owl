// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicMean implements TensorUnaryOperator {
  private static final List<Tensor> WEIGHTS = new ArrayList<>();

  /** @param radius
   * @return weights of kalman */
  static Tensor splits(int radius) {
    int width = 2 * radius + 1;
    Scalar weight = RationalScalar.of(1, width);
    Tensor w = Tensors.of(weight);
    for (int index = 0; index < radius - 1; ++index)
      w.append(weight.multiply(RealScalar.of(2)));
    Scalar p;
    Scalar factor = RealScalar.ONE;
    Tensor splits = Tensors.empty();
    for (int index = 0; index < radius; ++index) {
      p = w.Get(index).divide(factor);
      splits.append(p);
      factor = factor.multiply(RealScalar.ONE.subtract(p));
    }
    return Reverse.of(splits);
  }

  private final GeodesicInterface geodesicInterface;

  public GeodesicMean(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    int radius = (tensor.length() - 1) / 2;
    synchronized (WEIGHTS) {
      while (WEIGHTS.size() <= radius)
        WEIGHTS.add(splits(WEIGHTS.size()));
    }
    Tensor splits = WEIGHTS.get(radius);
    Tensor pL = tensor.get(0);
    Tensor pR = tensor.get(2 * radius);
    for (int index = 0; index < radius; ++index) {
      int pos = index + 1;
      Scalar scalar = splits.Get(index);
      pL = geodesicInterface.split(tensor.get(pos), pL, scalar);
      pR = geodesicInterface.split(tensor.get(2 * radius - pos), pR, scalar);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }
}
