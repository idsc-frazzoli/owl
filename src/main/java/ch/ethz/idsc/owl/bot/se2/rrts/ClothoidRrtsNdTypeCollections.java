// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.RrtsNdTypeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.Pi;

public enum ClothoidRrtsNdTypeCollections {
  ;
  /** @param lbounds vector of length 2
   * @param ubounds vector of length 2
   * @return */
  public static RrtsNodeCollection of(Tensor lbounds, Tensor ubounds) {
    return RrtsNdTypeCollection.of( //
        ClothoidRrtsNdType.INSTANCE, //
        VectorQ.requireLength(lbounds, 2).copy().append(Pi.VALUE.negate()), //
        VectorQ.requireLength(ubounds, 2).copy().append(Pi.VALUE));
  }
}
