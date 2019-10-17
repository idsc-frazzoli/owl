// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidCurvatureQuery implements TransitionRegionQuery, Serializable {
  private final Clip clip;

  /** @param clip with positive width */
  public ClothoidCurvatureQuery(Clip clip) {
    Sign.requirePositive(clip.width());
    this.clip = clip;
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    ClothoidTransition clothoidTransition = (ClothoidTransition) transition;
    HeadTailInterface headTailInterface = clothoidTransition.terminalRatios();
    return clip.isInside(headTailInterface.head()) //
        && clip.isInside(headTailInterface.tail());
  }
}
