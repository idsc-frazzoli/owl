// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.sophus.dubins.DubinsPath;
import ch.ethz.idsc.sophus.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.dubins.DubinsPathLengthComparator;
import ch.ethz.idsc.sophus.dubins.FixedRadiusDubins;
import ch.ethz.idsc.sophus.group.Se2CoveringGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Se2Transition extends AbstractTransition {
  private final DubinsPath dubinsPath;

  public Se2Transition(Tensor start, Tensor end, Scalar radius) {
    super(start, end);
    DubinsPathGenerator dubinsPathGenerator = //
        new FixedRadiusDubins(new Se2CoveringGroupElement(start).inverse().combine(end), radius);
    dubinsPath = dubinsPathGenerator.allValid() //
        .min(DubinsPathLengthComparator.INSTANCE).get();
  }

  @Override
  public Scalar length() {
    return dubinsPath.length();
  }

  public DubinsPath dubinsPath() {
    return dubinsPath;
  }

  @Override
  public List<StateTime> sampled(Scalar t0, Scalar ofs, Scalar dt) {
    // TODO RRTS Auto-generated method stub
    return null;
  }

  @Override
  public StateTime splitAt(Scalar t1) {
    // TODO RRTS Auto-generated method stub
    return null;
  }
}
