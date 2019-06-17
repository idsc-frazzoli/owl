// code by gjoel
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Se2NdType {
  EUCLIDEAN {
    @Override
    public Tensor convert(Tensor tensor) {
      return Extract2D.FUNCTION.apply(tensor);
    }

    @Override
    public NdCenterInterface getNdCenterInterface(Tensor tensor) {
      return NdCenterInterface.euclidean(convert(tensor));
    }
  },
  CLOTHOID {
    @Override
    public NdCenterInterface getNdCenterInterface(Tensor tensor) {
      return NdCenterInterface.clothoid(convert(tensor));
    }
  };
  /** @param tensor
   * @return tensor in right format */
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  /** @param tensor
   * @return */
  public abstract NdCenterInterface getNdCenterInterface(Tensor tensor);
}
