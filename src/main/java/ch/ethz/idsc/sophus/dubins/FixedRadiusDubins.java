// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.dubins;

import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.sophus.group.Se2CoveringGroupElement;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class FixedRadiusDubins implements DubinsPathGenerator {
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
    return Stream.of(DubinsPathType.values()) //
        .map(this::create) //
        .filter(Optional::isPresent) //
        .map(Optional::get);
  }

  private Optional<DubinsPath> create(DubinsPathType dubinsPathType) {
    Tensor center1 = Tensors.of(zero, radius, zero);
    Tensor h = Tensors.of(zero, dubinsPathType.isFirstEqualsLast() ? radius : radius.negate(), zero);
    Tensor gnorm = dubinsPathType.isFirstTurnRight() ? Se2Flip.FUNCTION.apply(xya) : xya;
    Tensor center3 = new Se2CoveringGroupElement(gnorm).combine(h);
    Tensor deltacenter = new Se2CoveringGroupElement(center1).inverse().combine(center3);
    // TODO use Scalar
    double dist_tr = Norm._2.ofVector(deltacenter.extract(0, 2)).number().doubleValue();
    double th_tr = ArcTan2D.of(deltacenter).number().doubleValue();
    double th_total = deltacenter.Get(2).number().doubleValue();
    th_tr = StaticHelper.principalValue(th_tr);
    th_total = StaticHelper.principalValue(th_total);
    return dubinsPathType.dubinsSteer().steer(dist_tr, th_tr, th_total, radius) //
        .map(segLength -> new DubinsPath(dubinsPathType, radius, segLength));
  }
}
