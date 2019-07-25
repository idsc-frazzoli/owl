// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatio;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public class TransitionCurvatureQuery implements TransitionRegionQuery {
  private final Clip clip;

  public TransitionCurvatureQuery(Number max) {
    this(RealScalar.of(max));
  }

  public TransitionCurvatureQuery(Scalar max) {
    clip = Clips.absolute(Sign.requirePositive(max));
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    ClothoidTerminalRatio curvatures = ((ClothoidTransition) transition).terminalRatios();
    return clip.isInside(curvatures.head()) && clip.isInside(curvatures.tail());
  }
}
