// code by jph
package ch.ethz.idsc.sophus.util;

import java.io.Serializable;
import java.util.function.Supplier;

public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
  // ---
}