// code by astoll and jph
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum BalloonStateSpaceModels {
  ;
  static BalloonStateSpaceModel defaultWithUnits() {
    return new BalloonStateSpaceModel( //
        Quantity.of(1, "s"), //
        Quantity.of(2, "s"), //
        Quantity.of(1, "m * K^-1 * s^-2"), //
        true);
  }

  static BalloonStateSpaceModel defaultWithoutUnits() {
    return new BalloonStateSpaceModel( //
        RealScalar.of(1), //
        RealScalar.of(2), //
        RealScalar.of(1), //
        false);
  }
}
