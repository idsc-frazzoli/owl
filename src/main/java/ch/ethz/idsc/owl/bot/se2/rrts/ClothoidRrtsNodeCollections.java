// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum ClothoidRrtsNodeCollections {
  ;
  /** Hint: the use of ClothoidNdType is the reliable choice
   * 
   * @param lbounds vector of length 2
   * @param ubounds vector of length 2
   * @return
   * @see ClothoidNdDemo */
  // TODO JPH OWL 056 remove
  public static RrtsNodeCollection of(Tensor lbounds, Tensor ubounds) {
    return Se2TransitionRrtsNodeCollections.of(ClothoidTransitionSpace.INSTANCE, lbounds, ubounds);
  }

  /** @param max
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(Scalar max, Tensor lbounds, Tensor ubounds) {
    return of(LimitedClothoidNdType.with(max), lbounds, ubounds);
  }

  private static RrtsNodeCollection of( //
      LimitedClothoidNdType limitedClothoidNdType, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of(limitedClothoidNdType, //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }
}
