// code by ynager
package ch.ethz.idsc.owl.data.img;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.TensorMap;

public class ImageTensors {
  private static final Scalar TFF = RealScalar.of(255);

  public static Tensor reduce(Tensor image, Tensor rgba) {
    return TensorMap.of(color -> color.equals(rgba) ? TFF : RealScalar.ZERO, image, 2);
  }

  public static Tensor reduce(Tensor image, int channel) {
    return TensorMap.of(color -> Scalars.isZero(color.Get(channel)) ? RealScalar.ZERO : TFF, image, 2);
  }

  public static Tensor reduceInverted(Tensor image, Tensor rgba) {
    return TensorMap.of(color -> color.equals(rgba) ? RealScalar.ZERO : TFF, image, 2);
  }

  public static Tensor reduceInverted(Tensor image, int channel) {
    return TensorMap.of(color -> Scalars.isZero(color.Get(channel)) ? TFF : RealScalar.ZERO, image, 2);
  }
}
