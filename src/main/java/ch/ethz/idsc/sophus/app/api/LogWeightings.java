// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.InverseCoordinate;
import ch.ethz.idsc.sophus.gbc.KrigingCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LogWeightings implements LogWeighting {
  DISTANCES() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.distances(vectorLogManifold, sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      throw new UnsupportedOperationException();
    }
  },
  /***************************************************/
  WEIGHTING() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.weighting(vectorLogManifold, variogram, sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  },
  /***************************************************/
  COORDINATE() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.coordinate(vectorLogManifold, variogram, sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  },
  /***************************************************/
  /** produces affine weights
   * restricted to certain variograms, e.g. power(1.5) */
  KRIGING() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return Kriging.barycentric(tensorUnaryOperator, sequence)::estimate;
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = //
          biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      Kriging kriging = Kriging.interpolation(tensorUnaryOperator, sequence, values);
      return point -> (Scalar) kriging.estimate(point);
    }
  }, //
  /***************************************************/
  KRIGING_COORDINATE() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return KrigingCoordinate.of(tensorUnaryOperator, vectorLogManifold, sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  INVERSE_COORDINATE() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return InverseCoordinate.of(tensorUnaryOperator, vectorLogManifold, sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  }, //
  ;

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }

  public static List<LogWeighting> coordinates() {
    return Arrays.asList( //
        COORDINATE, //
        KRIGING_COORDINATE, //
        INVERSE_COORDINATE);
  }
}
