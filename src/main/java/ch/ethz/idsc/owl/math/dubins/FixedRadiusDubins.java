// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Mod;

public class FixedRadiusDubins implements DubinsPathGenerator {
  private static final Scalar TWO_PI = RealScalar.of(2 * Math.PI);
  public static final double PI_HALF = Math.PI / 2;

  public static double principalValue(double angle) {
    return Mod.function(TWO_PI).apply(RealScalar.of(angle)).number().doubleValue();
  }

  /** helper function */
  private static Optional<Tensor> steer2turnsDiffSide(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    double aux = 2.0 * radius / dist_tr;
    if (1 < aux) // if intersecting, no tangent line
      return Optional.empty();
    double th_aux = Math.asin(aux);
    return Optional.of(Tensors.vector( //
        radius * principalValue(th_tr + th_aux), //
        dist_tr * Math.cos(th_aux), //
        radius * principalValue(th_tr + th_aux - th_total)));
  }

  /** helper function */
  private static Tensor steer2turnsSameSide(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    return Tensors.vector( //
        radius * th_tr, //
        dist_tr, //
        radius * principalValue(th_total - th_tr));
  }

  /** helper function */
  private static Optional<Tensor> steer3turns(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    double aux = dist_tr / 4.0 / radius;
    assert 0 <= aux;
    if (1 < aux)
      return Optional.empty();
    double th_aux = Math.acos(aux);
    return Optional.of(Tensors.vector( //
        radius * principalValue(th_tr + PI_HALF + th_aux), //
        radius * (Math.PI + 2.0 * th_aux), //
        radius * principalValue(th_total - th_tr + PI_HALF + th_aux)));
  }

  public static Optional<DubinsPath> create(Tensor g, DubinsPathType dubinsCurveType, final Scalar radius) {
    if (dubinsCurveType.isFirstTurnRight())
      g = Se2Flip.FUNCTION.apply(g);
    Tensor center1 = Tensors.of(RealScalar.ZERO, radius, RealScalar.ZERO);
    Tensor h = Tensors.of(RealScalar.ZERO, dubinsCurveType.isFirstEqualsLast() ? radius : radius.negate(), RealScalar.ZERO);
    Tensor center3 = new Se2CoveringGroupAction(g).combine(h);
    Tensor deltacenter = new Se2CoveringGroupAction(center1).inverse().combine(center3);
    double dist_tr = Norm._2.ofVector(deltacenter.extract(0, 2)).number().doubleValue();
    double th_tr = ArcTan.of(deltacenter.Get(0), deltacenter.Get(1)).number().doubleValue();
    double th_total = deltacenter.Get(2).number().doubleValue();
    th_tr = principalValue(th_tr);
    th_total = principalValue(th_total);
    if (dubinsCurveType.LSR_or_RSL) {
      Optional<Tensor> optional = steer2turnsDiffSide(dist_tr, th_tr, th_total, radius);
      if (optional.isPresent())
        return Optional.of(new DubinsPath(dubinsCurveType, radius, optional.get()));
      return Optional.empty();
    }
    if (dubinsCurveType.LSL_or_RSR) {
      Tensor steer2turnsSameSide = steer2turnsSameSide(dist_tr, th_tr, th_total, radius);
      return Optional.of(new DubinsPath(dubinsCurveType, radius, steer2turnsSameSide));
    }
    Optional<Tensor> steer3turns = steer3turns(dist_tr, th_tr, th_total, radius);
    if (steer3turns.isPresent())
      return Optional.of(new DubinsPath(dubinsCurveType, radius, steer3turns.get()));
    return Optional.empty();
  }

  private final Tensor g;
  private final Scalar radius;

  public FixedRadiusDubins(Tensor g, Scalar radius) {
    this.g = g;
    this.radius = radius;
  }

  @Override
  public Stream<DubinsPath> allValid() {
    return Stream.of(DubinsPathType.values()) //
        .map(dubinsPathType -> create(g, dubinsPathType, radius)) //
        .filter(Optional::isPresent) //
        .map(Optional::get);
  }
}
