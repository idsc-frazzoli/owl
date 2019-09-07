// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

public class TransitionCurvatureQuery implements TransitionRegionQuery {
  private final Clip clip;

  public TransitionCurvatureQuery(Clip clip) {
    Sign.requirePositive(clip.width());
    this.clip = clip;
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    HeadTailInterface headTailInterface = ((ClothoidTransition) transition).terminalRatios();
    return clip.isInside(headTailInterface.head()) //
        && clip.isInside(headTailInterface.tail());
  }
}
