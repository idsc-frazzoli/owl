// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Objects;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public class TransitionCurvatureQuery implements TransitionRegionQuery {
  private final Clip clip;

  public TransitionCurvatureQuery(Clip clip) {
    this.clip = Objects.requireNonNull(clip);
  }

  // TODO JPH OWL 054 remove
  public TransitionCurvatureQuery(Number max) {
    this(RealScalar.of(max));
  }

  // TODO JPH OWL 054 remove
  public TransitionCurvatureQuery(Scalar max) {
    clip = Clips.absolute(Sign.requirePositive(max));
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    HeadTailInterface headTailInterface = ((ClothoidTransition) transition).terminalRatios();
    return clip.isInside(headTailInterface.head()) //
        && clip.isInside(headTailInterface.tail());
  }
}
