// code by jph
package ch.ethz.idsc.tensor.ref;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ObjectPropertiesExtTest extends TestCase {
  public void testListSize1() throws Exception {
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerExt);
    paramContainerExt.string = "some string, no new line please";
    paramContainerExt.onlyInExt = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 2);
  }

  public void testListSize2() throws Exception {
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerExt);
    paramContainerExt.shape = Tensors.fromString("{1,2,3}");
    paramContainerExt.abc = RealScalar.ONE;
    paramContainerExt.maxTor = Scalars.fromString("3.13[m*s^2]");
    paramContainerExt.onlyInExt = Tensors.vector(3, 4, 9);
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 5);
  }

  public void testBoolean() throws Exception {
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerExt);
    paramContainerExt.status = true;
    assertEquals(tensorProperties.strings().size(), 3);
    Properties properties = tensorProperties.get();
    assertEquals(properties.getProperty("status"), "true");
    properties.setProperty("status", "corrupt");
    tensorProperties.set(properties);
    assertNull(paramContainerExt.status);
    assertEquals(tensorProperties.strings().size(), 2);
    // ---
    properties.setProperty("status", "true");
    tensorProperties.set(properties);
    assertTrue(paramContainerExt.status);
    assertEquals(tensorProperties.strings().size(), 3);
    // ---
    properties.setProperty("status", "false");
    tensorProperties.set(properties);
    assertFalse(paramContainerExt.status);
    assertEquals(tensorProperties.strings().size(), 3);
  }

  public void testStore() throws Exception {
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerExt);
    paramContainerExt.string = "some string, no new line please";
    assertEquals(tensorProperties.strings().size(), 2);
    paramContainerExt.maxTor = Scalars.fromString("3.13[m*s^2]");
    paramContainerExt.shape = Tensors.fromString("{1,2,3}");
    assertEquals(tensorProperties.strings().size(), 4);
    paramContainerExt.abc = RealScalar.ONE;
    assertEquals(tensorProperties.strings().size(), 5);
    Properties properties = tensorProperties.get();
    {
      ParamContainerExt pc = new ParamContainerExt();
      ObjectProperties tensorProperties2 = ObjectProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainerExt.string, pc.string);
      assertEquals(paramContainerExt.maxTor, pc.maxTor);
      assertEquals(paramContainerExt.shape, pc.shape);
      assertEquals(paramContainerExt.abc, pc.abc);
    }
    {
      ParamContainerExt pc = new ParamContainerExt();
      ObjectProperties tensorProperties2 = ObjectProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainerExt.string, pc.string);
      assertEquals(paramContainerExt.maxTor, pc.maxTor);
      assertEquals(paramContainerExt.shape, pc.shape);
      assertEquals(paramContainerExt.abc, pc.abc);
    }
  }

  public void testInsert() {
    Properties properties = new Properties();
    properties.setProperty("maxTor", "123[m]");
    properties.setProperty("shape", "{3   [s*kg],8*I}");
    properties.setProperty("onlyInExt", "{9,3   [m*kg],8*I}");
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    Field[] fields = ParamContainer.class.getFields();
    for (Field field : fields)
      if (!Modifier.isStatic(field.getModifiers()))
        try {
          Class<?> cls = field.getType();
          final String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string)) {
            if (cls.equals(Tensor.class))
              field.set(paramContainerExt, Tensors.fromString(string));
            else //
            if (cls.equals(Scalar.class))
              field.set(paramContainerExt, Scalars.fromString(string));
            else//
            if (cls.equals(String.class))
              field.set(paramContainerExt, string);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    assertTrue(paramContainerExt.maxTor instanceof Quantity);
    assertFalse(paramContainerExt.shape.stream().anyMatch(scalar -> scalar instanceof StringScalar));
    assertEquals(paramContainerExt.shape.length(), 2);
    // assertEquals(paramContainer.onlyInExt, Tensors.fromString("{9,3[m*kg],8*I}"));
  }

  public void testFields() {
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerExt);
    List<String> list = tensorProperties.fields().keySet().stream() //
        .map(Field::getName).collect(Collectors.toList());
    assertEquals(list.get(0), "onlyInExt");
    assertEquals(list.get(1), "string");
    assertEquals(list.get(2), "maxTor");
    assertEquals(list.get(3), "shape");
    assertEquals(list.get(4), "abc");
    assertEquals(list.get(5), "status");
  }

  public void testManifest() throws IOException {
    File file = TestFile.withExtension("properties");
    ObjectProperties tensorProperties = ObjectProperties.wrap(ParamContainerExt.INSTANCE);
    tensorProperties.save(file);
    assertTrue(file.isFile());
    ParamContainerExt paramContainerExt = new ParamContainerExt();
    ObjectProperties.wrap(paramContainerExt).load(file);
    assertTrue(file.delete());
    assertEquals(paramContainerExt.shape, Tensors.fromString("{-9, 2, 3  [m*s^-3], 8, 4}"));
    assertEquals(paramContainerExt.maxTor, Tensors.fromString("10[m*s]"));
    assertEquals(paramContainerExt.onlyInExt, Tensors.fromString("{9, 7}"));
  }

  public void testInstance() {
    assertEquals(new ParamContainerExt().string, "fromConstructor");
    assertEquals(ParamContainerExt.INSTANCE.string, "fromFile");
  }

  public void testTrySave() {
    File file = TestFile.withExtension("properties");
    ObjectProperties tensorProperties = ObjectProperties.wrap(new ParamContainerExt());
    assertTrue(tensorProperties.trySave(file));
    assertTrue(file.isFile());
    assertTrue(file.delete());
  }
}
