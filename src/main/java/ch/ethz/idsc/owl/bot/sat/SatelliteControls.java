// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.N;

/** controls for position and velocity */
public class SatelliteControls implements FlowsInterface, Serializable {
  private static final Tensor ZEROS = N.DOUBLE.of(Array.zeros(2));
  // ---
  private final Scalar amp;

  /** @param amp amplitude resolution (0, 1] */
  public SatelliteControls(Scalar amp) {
    this.amp = amp;
  }

  @Override
  public Collection<Flow> getFlows(int resolution) {
    StateSpaceModel stateSpaceModel = new SatelliteStateSpaceModel();
    Collection<Flow> collection = new HashSet<>();
    collection.add(StateSpaceModels.createFlow(stateSpaceModel, ZEROS));
    for (Tensor u : CirclePoints.of(resolution))
      collection.add(StateSpaceModels.createFlow(stateSpaceModel, u.multiply(amp)));
    return collection;
  }
}
