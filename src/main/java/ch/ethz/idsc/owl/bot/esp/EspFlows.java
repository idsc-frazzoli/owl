// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ch.ethz.idsc.owl.glc.core.StateTimeFlows;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum EspFlows implements StateTimeFlows {
  INSTANCE;

  private final Map<Tensor, Collection<Flow>> map = new HashMap<>();

  private EspFlows() {
    for (int px = 0; px <= 4; ++px)
      for (int py = 0; py <= 4; ++py)
        if (isField(px, py))
          map.put(Tensors.vector(px, py), flows(px, py));
  }

  @Override
  public Collection<Flow> flows(StateTime stateTime) {
    return map.get(stateTime.state().get(5));
  }

  private static Collection<Flow> flows(int px, int py) {
    Collection<Flow> collection = new LinkedList<>();
    for (int dx = -2; dx <= 2; ++dx)
      if (dx != 0 && isField(px + dx, py))
        collection.add(StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(dx, 0)));
    for (int dy = -2; dy <= 2; ++dy)
      if (dy != 0 && isField(px, py + dy))
        collection.add(StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(0, dy)));
    return collection;
  }

  static boolean isField(int px, int py) {
    boolean hi = 0 <= px && px <= 2 && 0 <= py && py <= 2;
    boolean lo = 2 <= px && px <= 4 && 2 <= py && py <= 4;
    return hi || lo;
  }
}
