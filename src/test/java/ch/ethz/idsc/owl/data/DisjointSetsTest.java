// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.IntStream;

import junit.framework.TestCase;

public class DisjointSetsTest extends TestCase {
  public void testSimple() {
    int n = 100;
    DisjointSets disjointSets = new DisjointSets();
    IntStream.range(0, n).forEach(i -> disjointSets.add());
    assertEquals(disjointSets.parents().size(), n);
    assertEquals(disjointSets.representatives().size(), n);
    Random random = new Random();
    // Distribution distribution = DiscreteUniformDistribution.of(0, n);
    for (int index = 0; index < n; ++index) {
      int scalar = random.nextInt(n);
      disjointSets.union(index, scalar);
    }
    {
      int maxDepth = IntStream.range(0, n).map(disjointSets::depth).reduce(Math::max).getAsInt();
      assertTrue(1 < maxDepth);
    }
    Collection<Integer> parents = disjointSets.parents(); // before representatives()
    Collection<Integer> representatives = disjointSets.representatives(); // invokes find() on all members
    assertFalse(parents.equals(representatives));
    {
      int maxDepth = IntStream.range(0, n).map(disjointSets::depth).reduce(Math::max).getAsInt();
      assertEquals(maxDepth, 1);
    }
    assertEquals(disjointSets.representatives(), disjointSets.parents());
    disjointSets.createMap(HashSet::new);
  }

  public void testSingle() {
    int n = 1000;
    DisjointSets disjointSets = new DisjointSets();
    IntStream.range(0, n).forEach(i -> disjointSets.add());
    for (int index = 0; index < n; ++index)
      disjointSets.union(index, 2);
    assertEquals(disjointSets.parents().size(), 1);
    // assertEquals(disjointSets.maxRank(), 1);
    assertEquals(disjointSets.parents(), disjointSets.representatives());
    assertEquals(disjointSets.parents().size(), 1);
    disjointSets.createMap(HashSet::new);
  }

  public void testDual() {
    int n = 1000;
    DisjointSets disjointSets = new DisjointSets();
    IntStream.range(0, n).forEach(i -> disjointSets.add());
    for (int index = 0; index < n; ++index)
      disjointSets.union(index, 100 + (index % 2));
    assertEquals(disjointSets.parents().size(), 2);
    // assertEquals(disjointSets.maxRank(), 1);
    assertEquals(disjointSets.parents(), disjointSets.representatives());
    assertEquals(disjointSets.parents().size(), 2);
    disjointSets.createMap(HashSet::new);
  }

  public void testSame() {
    int n = 1000;
    DisjointSets disjointSets = new DisjointSets();
    IntStream.range(0, n).forEach(i -> disjointSets.add());
    for (int index = 0; index < n; ++index)
      disjointSets.union(index, index);
    assertEquals(disjointSets.parents().size(), n);
    // assertEquals(disjointSet.maxRank(), 1);
    assertEquals(disjointSets.parents(), disjointSets.representatives());
    assertEquals(disjointSets.parents().size(), n);
    disjointSets.createMap(HashSet::new);
  }
}
