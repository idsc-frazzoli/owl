// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.Serializable;

import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;

/* package */ class Se2PseudoDistance implements Se2PseudoDistanceInterface, Serializable {
  private LieDifferences lieDifferences;
  private final Tensor p;
  private final Tensor q;

  public Se2PseudoDistance(Tensor p, Tensor q) {
    this.p = p.copy().unmodifiable();
    this.q = q.copy().unmodifiable();
  }
  
  
  public Tensor Se2PseudoDistance(Tensor p, Tensor q) {
    this.lieDifferences = new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    Scalar xy = Norm._2.ofVector(lieDifferences.pair(p, q).extract(0, 2));
    Scalar a = Norm._2.ofVector(lieDifferences.pair(p, q).extract(2, 3));
    return Tensors.of(xy, a);
  }
  
  public Tensor Se2CoveringPseudoDistance(Tensor p, Tensor q) {
    this.lieDifferences = new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    Scalar xy = Norm._2.ofVector(lieDifferences.pair(p, q).extract(0, 2));
    Scalar a = Norm._2.ofVector(lieDifferences.pair(p, q).extract(2, 3));
    return Tensors.of(xy, a);
  }
  
  @Override // from NdCenterInterface
  public Tensor Se2PseudoDistance() {
    return Se2PseudoDistance(p,q);
  }
}
