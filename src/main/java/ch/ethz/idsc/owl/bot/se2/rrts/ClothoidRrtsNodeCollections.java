// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.NdType;
import ch.ethz.idsc.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum ClothoidRrtsNodeCollections {
  ;
  /** @param max
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(Scalar max, Tensor lbounds, Tensor ubounds) {
    return of(LimitedClothoidNdType.with(max), lbounds, ubounds);
  }

  private static RrtsNodeCollection of(NdType ndType, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of(ndType, //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }
}
