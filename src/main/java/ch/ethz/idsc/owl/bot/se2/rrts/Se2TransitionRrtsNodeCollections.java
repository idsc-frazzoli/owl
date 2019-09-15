// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum Se2TransitionRrtsNodeCollections {
  ;
  /** Hint:
   * functionality for {@link ClothoidTransitionSpace} and {@link DubinsTransitionSpace}
   * 
   * @param transitionSpace
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of( //
        new TransitionNdType(transitionSpace), //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }
}
