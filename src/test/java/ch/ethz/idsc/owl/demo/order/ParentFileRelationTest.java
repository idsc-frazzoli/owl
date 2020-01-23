// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.io.File;

import junit.framework.TestCase;

public class ParentFileRelationTest extends TestCase {
  public void testSimple() {
    assertTrue(ParentFileRelation.INSTANCE.test(new File("/some/blub"), new File("/some/blub/")));
    assertTrue(ParentFileRelation.INSTANCE.test(new File("/some/blub"), new File("/some/blub/minor")));
    assertFalse(ParentFileRelation.INSTANCE.test(new File("/some/blub/more"), new File("/some/blub/minor")));
  }
}
