// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.map.Se2CoveringGroupElement;
import ch.ethz.idsc.owl.math.planar.ArcTan2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class FixedRadiusDubins implements DubinsPathGenerator {
  private final Tensor g;
  private final Scalar radius;

  public FixedRadiusDubins(Tensor g, Scalar radius) {
    this.g = g;
    this.radius = radius;
  }

  public Optional<DubinsPath> create(DubinsPathType dubinsPathType) {
    Tensor center1 = Tensors.of(RealScalar.ZERO, radius, RealScalar.ZERO);
    Tensor h = Tensors.of(RealScalar.ZERO, dubinsPathType.isFirstEqualsLast() ? radius : radius.negate(), RealScalar.ZERO);
    Tensor gnorm = dubinsPathType.isFirstTurnRight() ? Se2Flip.FUNCTION.apply(g) : g;
    Tensor center3 = new Se2CoveringGroupElement(gnorm).combine(h);
    Tensor deltacenter = new Se2CoveringGroupElement(center1).inverse().combine(center3);
    double dist_tr = Norm._2.ofVector(deltacenter.extract(0, 2)).number().doubleValue();
    double th_tr = ArcTan2D.of(deltacenter).number().doubleValue();
    double th_total = deltacenter.Get(2).number().doubleValue();
    th_tr = StaticHelper.principalValue(th_tr);
    th_total = StaticHelper.principalValue(th_total);
    return dubinsPathType.dubinsSteer().steer(dist_tr, th_tr, th_total, radius) //
        .map(segLength -> new DubinsPath(dubinsPathType, radius, segLength));
  }

  @Override
  public Stream<DubinsPath> allValid() {
    return Stream.of(DubinsPathType.values()) //
        .map(this::create) //
        .filter(Optional::isPresent) //
        .map(Optional::get);
  }
}
