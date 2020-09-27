// code by jph
package ch.ethz.idsc.tensor.ref;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.io.TensorProperties;
import junit.framework.TestCase;

public class FieldSubdivideTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    int ignored = 0;
    for (Field field : tensorProperties.fields().collect(Collectors.toList())) {
      FieldSubdivide fieldSubdivide = field.getAnnotation(FieldSubdivide.class);
      Serialization.copy(fieldSubdivide);
      Optional<Tensor> optional = TensorReflection.of(fieldSubdivide);
      switch (field.getName()) {
      case "scalar":
        assertEquals(optional.get(), Reverse.of(Range.of(0, 11)));
        break;
      case "quantity":
        assertEquals(optional.get(), Tensors.fromString("{1[m], 3/2[m], 2[m], 5/2[m], 3[m]}"));
        break;
      case "tensor":
        assertEquals(optional.get(), Tensors.fromString("{{10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}}"));
        break;
      default:
        ++ignored;
        break;
      }
    }
    assertEquals(ignored, 4);
  }
}
