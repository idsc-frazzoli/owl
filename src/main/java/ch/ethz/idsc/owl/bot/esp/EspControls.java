// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum EspControls {
  ;
  static final List<Flow> LIST = Arrays.asList( //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(-1, 0)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(+1, 0)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(0, -1)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(0, +1)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(-2, 0)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(+2, 0)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(0, -2)), //
      StateSpaceModels.createFlow(EspModel.INSTANCE, Tensors.vector(0, +2)));
}
