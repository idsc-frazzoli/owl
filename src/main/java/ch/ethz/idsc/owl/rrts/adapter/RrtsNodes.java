// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Objects;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;

public enum RrtsNodes {
  ;
  public static void costConsistency( //
      RrtsNode node, //
      TransitionSpace transitionSpace, //
      TransitionCostFunction transitionCostFunction) {
    boolean status = true;
    RrtsNode parent = node.parent();
    if (Objects.nonNull(parent)) {
      Scalar tran = node.costFromRoot().subtract(parent.costFromRoot());
      Transition transition = transitionSpace.connect(parent, node.state());
      Scalar tc = transitionCostFunction.cost(transition);
      status &= Scalars.isZero(Chop._10.of(tc.subtract(tran)));
      if (!status)
        throw TensorRuntimeException.of(tc, tran);
      status &= parent.costFromRoot().add(tran).equals(node.costFromRoot());
      if (!status)
        throw TensorRuntimeException.of(tc, tran);
    }
    for (RrtsNode child : node.children())
      costConsistency(child, transitionSpace, transitionCostFunction);
  }
}
