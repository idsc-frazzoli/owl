// code by jph
package ch.ethz.idsc.sophus.util;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class SerializableSupplierTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    SerializableSupplier<Tensor> serializableSupplier = () -> Tensors.empty();
    Supplier<Tensor> supplier = Serialization.copy(serializableSupplier);
    assertEquals(supplier.get(), Tensors.empty());
  }

  public void testOptional() throws ClassNotFoundException, IOException {
    SerializableSupplier<Optional<Tensor>> serializableSupplier = () -> Optional.of(RealScalar.ONE);
    Supplier<Optional<Tensor>> supplier = Serialization.copy(serializableSupplier);
    assertEquals(supplier.get().get(), RealScalar.ONE);
  }
}
