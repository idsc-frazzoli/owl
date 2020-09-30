// code by jph
package ch.ethz.idsc.tensor.ref;

import java.lang.reflect.Field;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class FieldClipTest extends TestCase {
  public void testSimple() {
    AnnotatedContainer paramContainer = new AnnotatedContainer();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainer);
    int ignored = 0;
    for (Field field : tensorProperties.fields().keySet()) {
      FieldClip fieldClip = field.getAnnotation(FieldClip.class);
      Optional<Clip> optional = TensorReflection.of(fieldClip);
      switch (field.getName()) {
      case "clipped":
        Clip clip = optional.get();
        assertEquals(clip.min(), RealScalar.of(2));
        assertEquals(clip.max(), RealScalar.of(6));
        break;
      default:
        ++ignored;
        break;
      }
    }
    assertEquals(ignored, 6);
  }
}
