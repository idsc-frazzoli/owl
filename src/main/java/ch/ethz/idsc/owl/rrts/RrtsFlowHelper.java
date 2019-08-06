// code by gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.function.BiFunction;

import ch.ethz.idsc.owl.bot.se2.rrts.Se2RrtsFlow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH OWL 050 obsolete
public enum RrtsFlowHelper {
  ;
  public static final BiFunction<StateTime, StateTime, Tensor> U_SE2 = Se2RrtsFlow::uBetween;
}
