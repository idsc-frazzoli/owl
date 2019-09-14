// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum DubinsRrtsNodeCollections {
  ;
  /** @param transitionSpace
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace, Tensor lbounds, Tensor ubounds) {
    return of(new TransitionNdType(transitionSpace), lbounds, ubounds);
  }

  private static RrtsNodeCollection of( //
      TransitionNdType transitionNdType, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of(transitionNdType, //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }
}
