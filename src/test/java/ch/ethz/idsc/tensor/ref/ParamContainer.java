// code by jph
package ch.ethz.idsc.tensor.ref;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// DO NOT CHANGE THE SPECIFIED VALUES
// TESTS REQUIRE THE SPECIFIED VALUES
/* package */ class ParamContainer {
  @FieldSubdivide(start = "10", end = "0", intervals = 10)
  public Scalar scalar = RealScalar.ONE;
  @FieldSubdivide(start = "1[m]", end = "3[m]", intervals = 4)
  public Scalar quantity = Quantity.of(1, "m");
  @FieldSubdivide(start = "{10, 0}", end = "{14, 0}", intervals = 4)
  public Tensor tensor = Tensors.vector(11, 0);
  /** fails because min is not a numeric string expression */
  @FieldSubdivide(start = "asd", end = "123", intervals = 2)
  public Scalar nocan;
  /** fails because intervals is not positive */
  @FieldSubdivide(start = "1", end = "2", intervals = 0)
  public Scalar wrong;
  public String text;
  @FieldClip(min = "2", max = "6")
  public Scalar clipped;
}
