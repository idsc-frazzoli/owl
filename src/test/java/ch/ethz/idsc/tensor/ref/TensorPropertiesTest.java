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

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TensorPropertiesTest extends TestCase {
  public void testParseString() {
    Object object = TensorProperties.parse(String.class, "ethz idsc ");
    assertEquals(object, "ethz idsc ");
  }

  public void testParseScalar() {
    Object object = TensorProperties.parse(Scalar.class, " 3/4+8*I[m*s^-2]");
    Scalar scalar = Quantity.of(ComplexScalar.of(RationalScalar.of(3, 4), RealScalar.of(8)), "m*s^-2");
    assertEquals(object, scalar);
  }

  public void testParseFile() {
    Object object = TensorProperties.parse(File.class, "/home/datahaki/file.txt");
    assertEquals(object, new File("/home/datahaki/file.txt"));
  }

  public void testParseBoolean() {
    Object object = TensorProperties.parse(Boolean.class, "true");
    assertEquals(object, Boolean.TRUE);
  }

  public void testIsTracked() {
    Field[] fields = ParamContainer.class.getFields();
    int count = 0;
    for (Field field : fields)
      count += TensorProperties.isTracked(field) ? 1 : 0;
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    int count2 = tensorProperties.fields().size();
    assertEquals(count, count2);
    assertEquals(count, 5);
  }

  public void testScalarClass() {
    Class<?> cls = RealScalar.ONE.getClass();
    assertFalse(cls.equals(Scalar.class));
    assertTrue(cls.equals(RationalScalar.class));
  }

  public void testParseTensorFail() {
    // FIXME
    Tensor tensor = Tensors.fromString("{1, 2}+a");
    System.out.println(tensor);
  }

  public void testParseScalarFail() {
    try {
      TensorProperties.parse(Integer.class, "123");
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testListSize1() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 2);
  }

  public void testListSize2() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.shape = Tensors.fromString("{1, 2, 3}");
    paramContainer.abc = RealScalar.ONE;
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = tensorProperties.strings();
    assertEquals(list.size(), 3);
  }

  public void testBoolean() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.status = true;
    assertEquals(tensorProperties.strings().size(), 1);
    Properties properties = tensorProperties.get();
    assertEquals(properties.getProperty("status"), "true");
    properties.setProperty("status", "corrupt");
    tensorProperties.set(properties);
    assertNull(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 0);
    // ---
    properties.setProperty("status", "true");
    tensorProperties.set(properties);
    assertTrue(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 1);
    // ---
    properties.setProperty("status", "false");
    tensorProperties.set(properties);
    assertFalse(paramContainer.status);
    assertEquals(tensorProperties.strings().size(), 1);
  }

  public void testStore() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    assertEquals(tensorProperties.strings().size(), 1);
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    paramContainer.shape = Tensors.fromString("{1, 2, 3}");
    assertEquals(tensorProperties.strings().size(), 3);
    paramContainer.abc = RealScalar.ONE;
    assertEquals(tensorProperties.strings().size(), 4);
    Properties properties = tensorProperties.get();
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainer.string, pc.string);
      assertEquals(paramContainer.maxTor, pc.maxTor);
      assertEquals(paramContainer.shape, pc.shape);
      assertEquals(paramContainer.abc, pc.abc);
    }
    {
      ParamContainer pc = new ParamContainer();
      TensorProperties tensorProperties2 = TensorProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainer.string, pc.string);
      assertEquals(paramContainer.maxTor, pc.maxTor);
      assertEquals(paramContainer.shape, pc.shape);
      assertEquals(paramContainer.abc, pc.abc);
    }
  }

  public void testInsert() {
    Properties properties = new Properties();
    properties.setProperty("maxTor", "123[m]");
    properties.setProperty("shape", "{3   [s*kg],8*I}");
    ParamContainer paramContainer = new ParamContainer();
    Field[] fields = ParamContainer.class.getFields();
    for (Field field : fields)
      if (!Modifier.isStatic(field.getModifiers()))
        try {
          Class<?> cls = field.getType();
          final String string = properties.getProperty(field.getName());
          if (Objects.nonNull(string)) {
            if (cls.equals(Tensor.class))
              field.set(paramContainer, Tensors.fromString(string));
            else //
            if (cls.equals(Scalar.class))
              field.set(paramContainer, Scalars.fromString(string));
            else//
            if (cls.equals(String.class))
              field.set(paramContainer, string);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    assertTrue(paramContainer.maxTor instanceof Quantity);
    assertTrue(paramContainer.shape.stream().noneMatch(scalar -> scalar instanceof StringScalar));
    assertEquals(paramContainer.shape.length(), 2);
  }

  public void testFields() {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    List<String> list = tensorProperties.fields().keySet().stream() //
        .map(Field::getName).collect(Collectors.toList());
    assertEquals(list.get(0), "string");
    assertEquals(list.get(1), "maxTor");
    assertEquals(list.get(2), "shape");
    assertEquals(list.get(3), "abc");
    assertEquals(list.get(4), "status");
  }

  public void testManifest() throws IOException {
    File file = TestFile.withExtension("properties");
    TensorProperties tensorProperties = TensorProperties.wrap(ParamContainer.INSTANCE);
    tensorProperties.save(file);
    assertTrue(file.isFile());
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties.wrap(paramContainer).load(file);
    assertEquals(paramContainer.shape, Tensors.fromString("{1, 2+I, 3[kg], 99}"));
    assertTrue(file.delete());
  }

  public void testTryLoad() {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    ParamContainer pc2 = tensorProperties.tryLoad(new File("fileDoesNotExist"));
    assertTrue(paramContainer == pc2);
  }

  public void testLoadFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new ParamContainer());
    try {
      tensorProperties.load(new File("fileDoesNotExist"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testTrySave() {
    File file = TestFile.withExtension("properties");
    TensorProperties tensorProperties = TensorProperties.wrap(new ParamContainer());
    assertTrue(tensorProperties.trySave(file));
    assertTrue(file.isFile());
    assertTrue(file.delete());
  }

  public void testTrySaveFail() {
    ParamContainer paramContainer = new ParamContainer();
    TensorProperties tensorProperties = TensorProperties.wrap(paramContainer);
    assertFalse(tensorProperties.trySave(new File("/home/cannot save here")));
  }

  public void testSetFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new ParamContainer());
    try {
      tensorProperties.set(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testWrapFail() {
    try {
      TensorProperties.wrap(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSerializationFail() {
    TensorProperties tensorProperties = TensorProperties.wrap(new ParamContainer());
    try {
      Serialization.copy(tensorProperties);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
