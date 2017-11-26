// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum R2dControls {
  ;
  public static Collection<Flow> createRadial(final int num) {
    GlobalAssert.that(2 < num); // otherwise does not cover plane
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    List<Flow> list = new ArrayList<>();
    for (Tensor a1 : CirclePoints.of(num)) {
      for (Tensor a2 : CirclePoints.of(num)) {
        list.add(StateSpaceModels.createFlow(stateSpaceModel, Join.of(a1, a2)));
        // FIXME add zero movement (at least for 2nd agent)
      }
    }
    return list;
  }
}
