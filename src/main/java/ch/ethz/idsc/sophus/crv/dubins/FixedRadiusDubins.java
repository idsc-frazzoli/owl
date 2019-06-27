// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.crv.dubins;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.crv.dubins.DubinsPath.Type;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroupElement;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class FixedRadiusDubins implements DubinsPathGenerator, Serializable {
  /** @param xya vector of length 3
   * @param radius
   * @return */
  public static DubinsPathGenerator of(Tensor xya, Scalar radius) {
    return new FixedRadiusDubins(xya, radius);
  }

  public static DubinsPathGenerator of(Tensor start, Tensor end, Scalar radius) {
    return of(new Se2CoveringGroupElement(start).inverse().combine(end), radius);
  }

  // ---
  private final Tensor xya;
  private final Scalar radius;
  private final Scalar zero;

  private FixedRadiusDubins(Tensor xya, Scalar radius) {
    this.xya = xya;
    this.radius = radius;
    zero = radius.zero();
  }

  @Override // from DubinsPathGenerator
  public Stream<DubinsPath> allValid() {
    return Stream.of(Type.values()) //
        .map(this::create) //
        .filter(Optional::isPresent) //
        .map(Optional::get);
  }

  private Optional<DubinsPath> create(Type dubinsPathType) {
    Tensor center1 = Tensors.of(zero, radius, xya.Get(2).zero());
    Tensor h = Tensors.of(zero, dubinsPathType.isFirstEqualsLast() ? radius : radius.negate(), xya.Get(2).zero());
    Tensor gnorm = dubinsPathType.isFirstTurnRight() ? Se2Flip.FUNCTION.apply(xya) : xya;
    Tensor center3 = new Se2CoveringGroupElement(gnorm).combine(h);
    Tensor deltacenter = new Se2CoveringGroupElement(center1).inverse().combine(center3);
    Scalar dist_tr = Norm._2.ofVector(deltacenter.extract(0, 2));
    Scalar th_tr = ArcTan2D.of(deltacenter);
    Scalar th_total = deltacenter.Get(2);
    th_tr = StaticHelper.principalValue(th_tr);
    th_total = StaticHelper.principalValue(th_total);
    return dubinsPathType.dubinsSteer().steer(dist_tr, th_tr, th_total, radius) //
        .map(segLength -> new DubinsPath(dubinsPathType, radius, segLength));
  }
}
