// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.gbc.Amplifiers;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;

public class IterativeAffineProperties {
  public Amplifiers amplifiers = Amplifiers.EXP;
  public Scalar beta = RealScalar.of(3);
  @FieldIntegerQ
  public Scalar refine = RealScalar.of(5);

  public Genesis genesis() {
    int resolution = refine.number().intValue();
    TensorUnaryOperator tensorUnaryOperator = amplifiers.supply(beta);
    return new IterativeAffineCoordinate(tensorUnaryOperator, resolution);
    // return new IterativeTargetCoordinate(InverseDistanceWeighting.of(InversePowerVariogram.of(2)), resolution);
  }
}
