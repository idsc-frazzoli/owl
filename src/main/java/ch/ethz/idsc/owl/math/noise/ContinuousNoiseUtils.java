// code by jph
package ch.ethz.idsc.owl.math.noise;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum ContinuousNoiseUtils {
  ;
  public static ScalarUnaryOperator wrap1D(NativeContinuousNoise nativeContinuousNoise) {
    return wrap1D(nativeContinuousNoise, RealScalar.ZERO);
  }

  public static ScalarUnaryOperator wrap1D(NativeContinuousNoise nativeContinuousNoise, Scalar offset) {
    double value = offset.number().doubleValue();
    return scalar -> RealScalar.of(nativeContinuousNoise.at(scalar.number().doubleValue(), value));
  }

  public static ContinuousNoise wrap2D(NativeContinuousNoise nativeContinuousNoise) {
    return tensor -> RealScalar.of(nativeContinuousNoise.at( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue()));
  }

  public static ContinuousNoise wrap3D(NativeContinuousNoise nativeContinuousNoise) {
    return tensor -> RealScalar.of(nativeContinuousNoise.at( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue(), //
        tensor.Get(2).number().doubleValue()));
  }
}
