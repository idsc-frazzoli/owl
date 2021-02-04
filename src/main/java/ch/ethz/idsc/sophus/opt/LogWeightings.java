// code by jph
package ch.ethz.idsc.sophus.opt;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.InverseCoordinate;
import ch.ethz.idsc.sophus.gbc.KrigingCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.itp.Kriging;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum LogWeightings implements LogWeighting {
  DISTANCES {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.distances(vectorLogManifold, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      throw new UnsupportedOperationException();
    }
  },
  /***************************************************/
  WEIGHTING {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.weighting(vectorLogManifold, variogram, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> (Scalar) tensorUnaryOperator.apply(point);
    }
  },
  /***************************************************/
  COORDINATE {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.coordinate(vectorLogManifold, variogram, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> (Scalar) tensorUnaryOperator.apply(point);
    }
  },
  /***************************************************/
  LAGRAINATE {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return biinvariant.lagrainate(vectorLogManifold, variogram, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> (Scalar) tensorUnaryOperator.apply(point);
    }
  },
  /***************************************************/
  /** produces affine weights
   * restricted to certain variograms, e.g. power(1.5) */
  KRIGING {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return Kriging.barycentric(tensorUnaryOperator, sequence)::estimate;
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = //
          biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      Kriging kriging = Kriging.interpolation(tensorUnaryOperator, sequence, values);
      return point -> (Scalar) kriging.estimate(point);
    }
  },
  /***************************************************/
  KRIGING_COORDINATE {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return KrigingCoordinate.of(tensorUnaryOperator, vectorLogManifold, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> (Scalar) tensorUnaryOperator.apply(point);
    }
  },
  INVERSE_COORDINATE {
    @Override // from LogWeighting
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = biinvariant.var_dist(vectorLogManifold, variogram, sequence);
      return InverseCoordinate.of(tensorUnaryOperator, vectorLogManifold, sequence);
    }

    @Override // from LogWeighting
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          operator(biinvariant, vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> (Scalar) tensorUnaryOperator.apply(point);
    }
  }, //
  ;

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }

  public static List<LogWeighting> coordinates() {
    return Arrays.asList( //
        COORDINATE, //
        LAGRAINATE, //
        KRIGING_COORDINATE, //
        INVERSE_COORDINATE);
  }

  public static List<LogWeighting> averagings() {
    return Arrays.asList( //
        WEIGHTING, //
        COORDINATE, //
        LAGRAINATE, //
        new NdTreeWeighting(4), //
        new NdTreeWeighting(6), //
        KRIGING, //
        KRIGING_COORDINATE, //
        INVERSE_COORDINATE);
  }
}
