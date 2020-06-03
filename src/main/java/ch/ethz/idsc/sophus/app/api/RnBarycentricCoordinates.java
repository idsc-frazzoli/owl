// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate barycentricCoordinate = R2BarycentricCoordinate.of(Barycenter.WACHSPRESS);
      return point -> barycentricCoordinate.weights(sequence, point);
    }
  },
  MEAN_VALUE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate barycentricCoordinate = R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE);
      return point -> barycentricCoordinate.weights(sequence, point);
    }
  },
  DISCRETE_HARMONIC() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate barycentricCoordinate = R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC);
      return point -> barycentricCoordinate.weights(sequence, point);
    }
  },
  AFFINE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return point -> AffineCoordinate.INSTANCE.weights(sequence, point);
    }
  },
  RBF() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      WeightingInterface weightingInterface = RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create(RnManifold.INSTANCE, r -> r));
      return point -> weightingInterface.weights(sequence, point);
    }
  }, //
  // TODO variogram
  RBF_INV_MULTI() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      WeightingInterface weightingInterface = RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create(flattenLogManifold, variogram));
      return point -> weightingInterface.weights(sequence, point);
    }
  },
  KR_ABSOLUTE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      WeightingInterface weightingInterface = PseudoDistances.ABSOLUTE.weighting(flattenLogManifold, variogram);
      return point -> weightingInterface.weights(sequence, point);
    }
  }, //
  ;

  public static List<LogWeighting> list() {
    List<LogWeighting> list = new ArrayList<>();
    list.addAll(LogWeightings.list());
    list.addAll(Arrays.asList(values()));
    return list;
  }

  public static List<LogWeighting> scattered() { //
    List<LogWeighting> list = new ArrayList<>();
    list.addAll(LogWeightings.list());
    list.add(AFFINE);
    list.add(RBF);
    list.add(RBF_INV_MULTI);
    list.add(KR_ABSOLUTE);
    return list;
  }
}
