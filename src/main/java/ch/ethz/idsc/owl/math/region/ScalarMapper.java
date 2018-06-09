// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;

public interface ScalarMapper<T> extends Function<Scalar, T>, Serializable {
  // ---
}