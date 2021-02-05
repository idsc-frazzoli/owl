// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.gbc.amp.Amplifiers;
import ch.ethz.idsc.sophus.gbc.it.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.gbc.it.IterativeTargetCoordinate;
import ch.ethz.idsc.sophus.itp.InverseDistanceWeighting;
import ch.ethz.idsc.sophus.math.Genesis;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;

public class GenesisDequeProperties {
  public Boolean lagrange = false;
  public Amplifiers amplifiers = Amplifiers.EXP;
  public Scalar beta = RealScalar.of(3);
  @FieldIntegerQ
  public Scalar refine = RealScalar.of(20);

  public Genesis genesis() {
    int resolution = refine.number().intValue();
    TensorUnaryOperator tensorUnaryOperator = amplifiers.supply(beta);
    return lagrange //
        ? new IterativeTargetCoordinate(InverseDistanceWeighting.of(InversePowerVariogram.of(2)), beta, resolution)
        : new IterativeAffineCoordinate(tensorUnaryOperator, resolution);
  }
}
