// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.owl.math.region.Region;
import junit.framework.TestCase;
import lcm.util.ClassDiscovery;
import lcm.util.ClassPaths;
import lcm.util.ClassVisitor;

public class DiscoveryTest extends TestCase {
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
    // System.out.println(regions.size());
    assertTrue(30 < regions.size());
  }
}
