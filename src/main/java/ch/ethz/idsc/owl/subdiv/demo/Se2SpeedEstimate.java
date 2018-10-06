// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Mod;

enum Se2SpeedEstimate implements TensorUnaryOperator {
  FUNCTION;
  // ---
  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD_DISTANCE = Mod.function(Math.PI * 2, -Math.PI);

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    Tensor prev = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index) {
      Tensor next = tensor.get(index);
      Tensor delta = new Se2CoveringGroupAction(prev).inverse().combine(next);
      delta.set(MOD_DISTANCE, INDEX_ANGLE);
      result.append(Se2CoveringExponential.INSTANCE.log(delta));
      prev = next;
    }
    return result;
  }
}
