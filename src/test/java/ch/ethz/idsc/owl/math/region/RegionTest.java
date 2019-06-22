// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import lcm.util.ClassDiscovery;
import lcm.util.ClassPaths;
import lcm.util.ClassVisitor;

public class RegionTest extends TestCase {
  public void testSimple() {
    Set<Class<?>> regions = new HashSet<>();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void classFound(String jarfile, Class<?> cls) {
        if (Region.class.isAssignableFrom(cls))
          regions.add(cls);
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    assertTrue(60 < regions.size());
  }
}
