// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import ch.ethz.idsc.sophus.gbc.Amplifiers;
import ch.ethz.idsc.sophus.gbc.DequeGenesis;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.gbc.IterativeTargetCoordinate;
import ch.ethz.idsc.sophus.krg.InverseDistanceWeighting;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;

public class DequeGenesisProperties {
  public Boolean lagrange = false;
  public Amplifiers amplifiers = Amplifiers.EXP;
  public Scalar beta = RealScalar.of(3);
  @FieldIntegerQ
  public Scalar refine = RealScalar.of(20);

  public DequeGenesis dequeGenesis() {
    int resolution = refine.number().intValue();
    TensorUnaryOperator tensorUnaryOperator = amplifiers.supply(beta);
    return lagrange //
        ? new IterativeTargetCoordinate(InverseDistanceWeighting.of(InversePowerVariogram.of(2)), resolution)
        : new IterativeAffineCoordinate(tensorUnaryOperator, resolution);
  }
}
