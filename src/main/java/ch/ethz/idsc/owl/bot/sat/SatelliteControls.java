// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.util.Collection;
import java.util.HashSet;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/** controls for position and velocity */
public enum SatelliteControls {
  ;
  /** radial
   * 
   * @param mu coefficient, any real number
   * @param seg amplitude resolution (0, 1]
   * @param num angular resolution
   * @return */
  public static Collection<Flow> create2d(int num) {
    StateSpaceModel stateSpaceModel = new SatelliteStateSpaceModel();
    Collection<Flow> collection = new HashSet<>();
    // collection.add(StateSpaceModels.createFlow(stateSpaceModel, Array.zeros(2)));
    for (Tensor u : CirclePoints.of(num))
      collection.add(StateSpaceModels.createFlow(stateSpaceModel, u));
    return collection;
  }
}
