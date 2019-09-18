// code by jph
package ch.ethz.idsc.sophus.crv;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.LieGroupLineDistance.NormImpl;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieMidpointInterface;
import ch.ethz.idsc.tensor.Tensor;

public class LieMidpointLineDistance implements LineDistance, Serializable {
  private final LieMidpointInterface lieMidpointInterface;
  private final LieGroupLineDistance lieGroupLineDistance;

  public LieMidpointLineDistance(LieGroup lieGroup, LieExponential lieExponential) {
    lieMidpointInterface = new LieMidpointInterface(lieGroup, lieExponential);
    lieGroupLineDistance = new LieGroupLineDistance(lieGroup, lieExponential::log);
  }

  @Override // from LineDistance
  public NormImpl tensorNorm(Tensor p, Tensor q) {
    return lieGroupLineDistance.tensorNorm(lieMidpointInterface.midpoint(p, q), q);
  }
}
