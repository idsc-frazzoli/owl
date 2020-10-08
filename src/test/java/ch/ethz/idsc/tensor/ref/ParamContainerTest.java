// code by jph
package ch.ethz.idsc.tensor.ref;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ParamContainerTest extends TestCase {
  public void testSimple() {
    ParamContainer paramContainer = ParamContainer.INSTANCE;
    assertTrue(paramContainer.maxTor instanceof Quantity);
    assertEquals(paramContainer.shape.length(), 4);
  }

  public void testFile() {
    ParamContainerFile paramContainerFile = new ParamContainerFile();
    paramContainerFile.file = HomeDirectory.file("file.txt");
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerFile);
    Properties properties = tensorProperties.get();
    ParamContainerFile copy = new ParamContainerFile();
    ObjectProperties wrap = ObjectProperties.wrap(copy);
    wrap.set(properties);
    assertEquals(copy.file, paramContainerFile.file);
  }

  public void testExportImport() throws IOException {
    ParamContainerFile paramContainerFile = new ParamContainerFile();
    paramContainerFile.text = "abc";
    paramContainerFile.tensor = Tensors.vector(1, 2, 3);
    paramContainerFile.file = HomeDirectory.file("file.txt");
    ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerFile);
    File storage = TestFile.withExtension("properties");
    tensorProperties.save(storage);
    ParamContainerFile loaded = ObjectProperties.wrap(new ParamContainerFile()).tryLoad(storage);
    assertEquals(loaded.file, paramContainerFile.file);
    assertEquals(loaded.tensor, paramContainerFile.tensor);
    assertEquals(loaded.text, paramContainerFile.text);
    storage.delete();
  }

  public void testEnumPropagate() {
    Properties properties;
    {
      ParamContainerEnum paramContainerEnum = new ParamContainerEnum();
      paramContainerEnum.pivots = Pivots.FIRST_NON_ZERO;
      paramContainerEnum.nameString = NameString.SECOND;
      ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerEnum);
      properties = tensorProperties.get();
    }
    {
      ParamContainerEnum paramContainerEnum = new ParamContainerEnum();
      ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerEnum);
      tensorProperties.set(properties);
      assertEquals(paramContainerEnum.pivots, Pivots.FIRST_NON_ZERO);
      assertEquals(paramContainerEnum.nameString, NameString.SECOND);
    }
  }

  public void testEnumNull() {
    Properties properties;
    {
      ParamContainerEnum paramContainerEnum = new ParamContainerEnum();
      ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerEnum);
      properties = tensorProperties.get();
    }
    properties.setProperty("nameString", NameString.THIRD.name());
    {
      ParamContainerEnum paramContainerEnum = new ParamContainerEnum();
      ObjectProperties tensorProperties = ObjectProperties.wrap(paramContainerEnum);
      tensorProperties.set(properties);
      assertNull(paramContainerEnum.pivots);
      assertEquals(paramContainerEnum.nameString, NameString.THIRD);
    }
  }
}
