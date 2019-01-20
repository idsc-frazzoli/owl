// code by jph
package ch.ethz.idsc.tensor;

import java.util.stream.Stream;

public enum TensorsExt {
  ;
  public static Tensor of(Object... objects) {
    return Tensor.of(Stream.of(objects).map(TensorsExt::fromObject));
  }

  private static Tensor fromObject(Object object) {
    if (object instanceof Tensor)
      return ((Tensor) object).copy();
    if (object instanceof Number)
      return RealScalar.of((Number) object);
    throw new RuntimeException();
  }
}
