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

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ObjectPropertiesTest extends TestCase {
  public void testParseString() {
    Object object = ObjectProperties.parse(String.class, "ethz idsc ");
    assertEquals(object, "ethz idsc ");
  }

  public void testParseScalar() {
    Object object = ObjectProperties.parse(Scalar.class, " 3/4+8*I[m*s^-2]");
    Scalar scalar = Quantity.of(ComplexScalar.of(RationalScalar.of(3, 4), RealScalar.of(8)), "m*s^-2");
    assertEquals(object, scalar);
  }

  public void testParseFile() {
    Object object = ObjectProperties.parse(File.class, "/home/datahaki/file.txt");
    assertEquals(object, new File("/home/datahaki/file.txt"));
  }

  public void testParseBoolean() {
    Object object = ObjectProperties.parse(Boolean.class, "true");
    assertEquals(object, Boolean.TRUE);
  }

  public void testIsTracked() {
    Field[] fields = ParamContainer.class.getFields();
    int count = 0;
    for (Field field : fields)
      count += ObjectProperties.isTracked(field) ? 1 : 0;
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    int count2 = objectProperties.fields().size();
    assertEquals(count, count2);
    assertEquals(count, 5);
  }

  public void testScalarClass() {
    Class<?> cls = RealScalar.ONE.getClass();
    assertFalse(cls.equals(Scalar.class));
    assertTrue(cls.equals(RationalScalar.class));
  }

  public void testParseTensorFail() {
    Tensor tensor = Tensors.fromString("{1, 2}+a");
    assertTrue(tensor instanceof StringScalar);
  }

  public void testParseScalarFail() {
    AssertFail.of(() -> ObjectProperties.parse(Integer.class, "123"));
  }

  public void testListSize1() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = objectProperties.strings();
    assertEquals(list.size(), 2);
  }

  public void testListSize2() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    paramContainer.shape = Tensors.fromString("{1, 2, 3}");
    paramContainer.abc = RealScalar.ONE;
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    List<String> list = objectProperties.strings();
    assertEquals(list.size(), 3);
  }

  public void testBoolean() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    paramContainer.status = true;
    assertEquals(objectProperties.strings().size(), 1);
    Properties properties = objectProperties.get();
    assertEquals(properties.getProperty("status"), "true");
    properties.setProperty("status", "corrupt");
    objectProperties.set(properties);
    assertNull(paramContainer.status);
    assertEquals(objectProperties.strings().size(), 0);
    // ---
    properties.setProperty("status", "true");
    objectProperties.set(properties);
    assertTrue(paramContainer.status);
    assertEquals(objectProperties.strings().size(), 1);
    // ---
    properties.setProperty("status", "false");
    objectProperties.set(properties);
    assertFalse(paramContainer.status);
    assertEquals(objectProperties.strings().size(), 1);
  }

  public void testStore() throws Exception {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    paramContainer.string = "some string, no new line please";
    assertEquals(objectProperties.strings().size(), 1);
    paramContainer.maxTor = Scalars.fromString("3.13[m*s^2]");
    paramContainer.shape = Tensors.fromString("{1, 2, 3}");
    assertEquals(objectProperties.strings().size(), 3);
    paramContainer.abc = RealScalar.ONE;
    assertEquals(objectProperties.strings().size(), 4);
    Properties properties = objectProperties.get();
    {
      ParamContainer pc = new ParamContainer();
      ObjectProperties tensorProperties2 = ObjectProperties.wrap(pc);
      tensorProperties2.set(properties);
      assertEquals(paramContainer.string, pc.string);
      assertEquals(paramContainer.maxTor, pc.maxTor);
      assertEquals(paramContainer.shape, pc.shape);
      assertEquals(paramContainer.abc, pc.abc);
    }
    {
      ParamContainer pc = new ParamContainer();
      ObjectProperties tensorProperties2 = ObjectProperties.wrap(pc);
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
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    List<String> list = objectProperties.fields().keySet().stream() //
        .map(Field::getName).collect(Collectors.toList());
    assertEquals(list.get(0), "string");
    assertEquals(list.get(1), "maxTor");
    assertEquals(list.get(2), "shape");
    assertEquals(list.get(3), "abc");
    assertEquals(list.get(4), "status");
  }

  public void testManifest() throws IOException {
    File file = TestFile.withExtension("properties");
    ObjectProperties objectProperties = ObjectProperties.wrap(ParamContainer.INSTANCE);
    objectProperties.save(file);
    assertTrue(file.isFile());
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties.wrap(paramContainer).load(file);
    assertEquals(paramContainer.shape, Tensors.fromString("{1, 2+I, 3[kg], 99}"));
    assertTrue(file.delete());
  }

  public void testTryLoad() {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    ParamContainer pc2 = objectProperties.tryLoad(new File("fileDoesNotExist"));
    assertTrue(paramContainer == pc2);
  }

  public void testLoadFail() {
    ObjectProperties tensorProperties = ObjectProperties.wrap(new ParamContainer());
    try {
      tensorProperties.load(new File("fileDoesNotExist"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testTrySave() {
    File file = TestFile.withExtension("properties");
    ObjectProperties objectProperties = ObjectProperties.wrap(new ParamContainer());
    assertTrue(objectProperties.trySave(file));
    assertTrue(file.isFile());
    assertTrue(file.delete());
  }

  public void testTrySaveFail() {
    ParamContainer paramContainer = new ParamContainer();
    ObjectProperties objectProperties = ObjectProperties.wrap(paramContainer);
    assertFalse(objectProperties.trySave(new File("/home/cannot save here")));
  }

  public void testSetFail() {
    ObjectProperties objectProperties = ObjectProperties.wrap(new ParamContainer());
    AssertFail.of(() -> objectProperties.set(null));
  }

  public void testWrapFail() {
    AssertFail.of(() -> ObjectProperties.wrap(null));
  }

  public void testSerializationFail() {
    ObjectProperties objectProperties = ObjectProperties.wrap(new ParamContainer());
    try {
      Serialization.copy(objectProperties);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
