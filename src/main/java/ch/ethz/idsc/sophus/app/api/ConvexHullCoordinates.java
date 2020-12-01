// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.Amplifiers;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.HsCoordinates;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.gbc.IterativeAffineGenesis;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.r2.InsideConvexHullCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

public enum ConvexHullCoordinates implements LogWeighting {
  AFFINE_00_LO(new IterativeAffineCoordinate(Amplifiers.EXP.supply(1), 0)), //
  // ---
  EXPONENTIAL_01_LO(new IterativeAffineCoordinate(Amplifiers.EXP.supply(1), 1)), //
  SMOOTH_RAMP_01_LO(new IterativeAffineCoordinate(Amplifiers.RAMP.supply(1), 1)), //
  ARCTAN_01_LO(new IterativeAffineCoordinate(Amplifiers.ARCTAN.supply(1), 1)), //
  // ---
  AFFINE_01_HI(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 1)), //
  AFFINE_05_LO(new IterativeAffineCoordinate(Amplifiers.EXP.supply(1), 5)), //
  AFFINE_05_HI(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 5)), //
  EXPONENTIAL_LO(new IterativeAffineGenesis(Amplifiers.EXP.supply(1), Chop._08)), //
  EXPONENTIAL_HI(new IterativeAffineGenesis(Amplifiers.EXP.supply(5), Chop._08)), //
  SMOOTH_RAMP_LO(new IterativeAffineGenesis(Amplifiers.RAMP.supply(1), Chop._08)), //
  SMOOTH_RAMP_HI(new IterativeAffineGenesis(Amplifiers.RAMP.supply(5), Chop._08)), //
  ARCTAN_LO(new IterativeAffineGenesis(Amplifiers.ARCTAN.supply(5), Chop._08)), //
  ARCTAN_HI(new IterativeAffineGenesis(Amplifiers.ARCTAN.supply(10), Chop._08)), //
  ;

  private final Genesis genesis;

  private ConvexHullCoordinates(Genesis genesis) {
    this.genesis = genesis;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        HsCoordinates.wrap(vectorLogManifold, InsideConvexHullCoordinate.of(genesis)), //
        sequence);
  }

  @Override // from LogWeighting
  public TensorScalarFunction function( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence, Tensor values) {
    TensorUnaryOperator tensorUnaryOperator = operator(biinvariant, vectorLogManifold, variogram, sequence);
    Objects.requireNonNull(values);
    return point -> tensorUnaryOperator.apply(point).dot(values).Get();
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }

  public Genesis genesis() {
    return genesis;
  }
}
