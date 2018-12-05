// code by astoll and jph
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum BalloonStateSpaceModels {
  ;
  static BalloonStateSpaceModel defaultWithUnits() {
    return new BalloonStateSpaceModel( //
        Quantity.of(1, "s"), //
        Quantity.of(2, "s"), //
        Quantity.of(1, "m * K^-1 * s^-2"));
  }
}
