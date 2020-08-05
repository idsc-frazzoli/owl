// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.gbc.Barycenter;
import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.gbc.D2BarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum D2BarycentricCoordinates implements LogWeighting {
  /** only for 2-dimensional tangent space */
  WACHSPRESS(Barycenter.WACHSPRESS), //
  /** only for 2-dimensional tangent space */
  MEAN_VALUE(Barycenter.MEAN_VALUE), //
  /** only for 2-dimensional tangent space */
  DISCRETE_HARMONIC(Barycenter.DISCRETE_HARMONIC),;

  private final Barycenter barycenter;

  private D2BarycentricCoordinates(Barycenter barycenter) {
    this.barycenter = barycenter;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return WeightingOperators.wrap( //
        D2BarycentricCoordinate.of(vectorLogManifold, barycenter), //
        sequence);
  }

  @Override // from LogWeighting
  public TensorScalarFunction function( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence, Tensor values) {
    BarycentricCoordinate barycentricCoordinate = D2BarycentricCoordinate.of(vectorLogManifold, barycenter);
    Objects.requireNonNull(sequence);
    Objects.requireNonNull(values);
    return point -> barycentricCoordinate.weights(sequence, point).dot(values).Get();
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
