// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.adapter.NdType;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class LimitedClothoidNdType implements NdType, Serializable {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  /** @param max curvature non-negative
   * @return */
  public static NdType with(Scalar max) {
    return new LimitedClothoidNdType(Clips.absolute(max));
  }

  /** @param max curvature non-negative
   * @return */
  public static NdType with(Number max) {
    return with(RealScalar.of(max));
  }

  /***************************************************/
  private final Clip clip;

  /** @param clip non-null */
  private LimitedClothoidNdType(Clip clip) {
    this.clip = clip;
  }

  @Override // from NdType
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new LimitedClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return CLOTHOID_BUILDER.curve(other, center);
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new LimitedClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return CLOTHOID_BUILDER.curve(center, other);
      }
    };
  }

  private static Scalar infinity(Scalar cost) {
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(cost));
  }

  /***************************************************/
  /* package */ abstract class LimitedClothoidNdCenter implements NdCenterInterface, Serializable {
    private final Tensor center;

    public LimitedClothoidNdCenter(Tensor center) {
      this.center = center;
    }

    @Override // from NdCenterInterface
    public Tensor center() {
      return center.unmodifiable();
    }

    @Override // from ClothoidNdCenter
    public final Scalar distance(Tensor p) {
      Clothoid clothoid = clothoid(p);
      Scalar cost = clothoid.length();
      return clip.isInside(clothoid.curvature().absMax()) //
          ? cost
          : infinity(cost);
    }

    /** @param other
     * @return clothoid either from center to other, or from other to center */
    protected abstract Clothoid clothoid(Tensor other);
  }
}
