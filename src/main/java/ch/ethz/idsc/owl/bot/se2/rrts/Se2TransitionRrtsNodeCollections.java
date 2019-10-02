// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.adapter.TransitionNdType;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** nearest-neighbor query heuristic backed by NdTreeMap */
// TODO JPH OWL 057 rename to Se2RrtsNodeCollections
public enum Se2TransitionRrtsNodeCollections {
  ;
  private static final Scalar ZERO = RealScalar.of(0.0);

  /** Hint:
   * functionality for {@link ClothoidTransitionSpace} and {@link DubinsTransitionSpace}
   * 
   * @param transitionSpace
   * @param lbounds vector of length 2
   * @param ubounds vector of length 2
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of( //
        new TransitionNdType(transitionSpace), //
        VectorQ.requireLength(lbounds, 2).copy().append(ZERO), //
        VectorQ.requireLength(ubounds, 2).copy().append(ZERO));
  }
}
