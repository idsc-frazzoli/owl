// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ class ClothoidNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;
  private Clip clip = null;

  public ClothoidNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    Scalar cost = ClothoidParametricDistance.INSTANCE.distance(vector, center);
    if (Objects.isNull(clip) || //
        (clip.isInside(new Clothoid(vector, center).new Curvature().head()) && clip.isInside(new Clothoid(vector, center).new Curvature().tail())))
      return cost;
    Scalar inf = RealScalar.of(Double.MAX_VALUE);
    return cost instanceof Quantity //
        ? Quantity.of(inf, ((Quantity) cost).unit()) //
        : inf;
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }

  /* package */ void limitCurvature(Clip clip) {
    this.clip = clip;
  }
}
