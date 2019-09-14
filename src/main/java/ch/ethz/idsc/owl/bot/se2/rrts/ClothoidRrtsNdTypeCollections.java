// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.RrtsNdTypeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.tensor.RealScalar;
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
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }

  public static RrtsNodeCollection of( //
      LimitedClothoidRrtsNdType limitedClothoidRrtsNdType, Tensor lbounds, Tensor ubounds) {
    return RrtsNdTypeCollection.of( //
        limitedClothoidRrtsNdType, //
        VectorQ.requireLength(lbounds, 2).copy().append(RealScalar.of(0.0)), //
        VectorQ.requireLength(ubounds, 2).copy().append(RealScalar.of(0.0)));
  }

  /** @param lbounds vector of length 2
   * @param ubounds vector of length 2
   * @return */
  public static RrtsNodeCollection angle(Tensor lbounds, Tensor ubounds) {
    return RrtsNdTypeCollection.of( //
        ClothoidRrtsNdType.INSTANCE, //
        VectorQ.requireLength(lbounds, 2).copy().append(Pi.VALUE.negate()), //
        VectorQ.requireLength(ubounds, 2).copy().append(Pi.VALUE));
  }

  public static RrtsNodeCollection angle( //
      LimitedClothoidRrtsNdType limitedClothoidRrtsNdType, Tensor lbounds, Tensor ubounds) {
    return RrtsNdTypeCollection.of( //
        limitedClothoidRrtsNdType, //
        VectorQ.requireLength(lbounds, 2).copy().append(Pi.VALUE.negate()), //
        VectorQ.requireLength(ubounds, 2).copy().append(Pi.VALUE));
  }
}
