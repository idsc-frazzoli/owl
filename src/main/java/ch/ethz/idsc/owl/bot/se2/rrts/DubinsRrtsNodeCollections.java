// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum DubinsRrtsNodeCollections {
  ;
  /** @param radius
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(Scalar radius, Tensor lbounds, Tensor ubounds) {
    return of(new DubinsNdType(radius), lbounds, ubounds);
  }

  private static RrtsNodeCollection of( //
      DubinsNdType dubinsNdType, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of(dubinsNdType, //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }
}
