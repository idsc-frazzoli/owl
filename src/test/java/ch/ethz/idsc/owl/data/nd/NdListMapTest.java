// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.BernoulliDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class NdListMapTest extends TestCase {
  public void testSimple() {
    NdMap<String> m1 = new NdListMap<>();
    m1.add(Tensors.vector(1, 0), "p2");
    m1.add(Tensors.vector(1, 5), "p4");
    m1.add(Tensors.vector(0, 0), "p1");
    m1.add(Tensors.vector(1, 1), "p3");
    Tensor center = Tensors.vector(0, 0);
    NdCluster<String> cl = m1.buildCluster(NdCenterInterface.euclidean(center), 2);
    Set<String> res = cl.stream().map(NdEntry::value).collect(Collectors.toSet());
    assertTrue(res.contains("p1"));
    assertTrue(res.contains("p2"));
    assertEquals(res.size(), 2);
  }

  private static Scalar addDistances(NdCluster<String> cluster, Tensor center, NdCenterInterface d) {
    Scalar sum = RealScalar.ZERO;
    for (NdEntry<String> entry : cluster.collection()) {
      Scalar dist = d.ofVector(entry.location());
      assertEquals(entry.distance(), dist);
      sum = sum.add(dist);
    }
    return sum;
  }

  private static void _checkCenter(Tensor center, int n, int dim, int dep) {
    NdMap<String> m1 = new NdListMap<>();
    NdMap<String> m2 = new NdTreeMap<>(Tensors.vector(-2, -1), Tensors.vector(2, 10), dim, dep);
    int index = 0;
    Distribution b = BernoulliDistribution.of(RealScalar.of(.25));
    Distribution ux = UniformDistribution.of(-2, 2);
    Distribution uy = UniformDistribution.of(-1, 10);
    for (int c = 0; c < 20; ++c) {
      Tensor location = Tensors.of(RandomVariate.of(ux), RandomVariate.of(uy));
      String value = "p" + (++index);
      m1.add(location, value);
      m2.add(location, value);
      while (Scalars.isZero(RandomVariate.of(b))) {
        value = "p" + (++index);
        m1.add(location, value);
        m2.add(location, value);
      }
    }
    // System.out.println(m1.size());
    assertEquals(m1.size(), m2.size());
    NdCenterInterface dinf = NdCenterInterface.euclidean(center);
    NdCluster<String> c1 = m1.buildCluster(dinf, n);
    NdCluster<String> c2 = m2.buildCluster(dinf, n);
    assertEquals(c1.size(), c2.size());
    assertTrue(c1.size() <= n);
    Scalar s1 = addDistances(c1, center, dinf);
    Scalar s2 = addDistances(c2, center, dinf);
    if (!Chop._11.close(s1, s2)) {
      System.out.println(s1);
      System.out.println(s2);
    }
    assertTrue(Chop._11.close(s1, s2));
    // System.out.println("considered " + c2.considered() + " / " + m2.size());
  }

  public void testOne() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(.3, .3), 1, dim, 6);
      _checkCenter(Tensors.vector(.1, .3), 1, dim, 6);
      _checkCenter(Tensors.vector(5, 4.3), 1, dim, 10);
      _checkCenter(Tensors.vector(5, -3.3), 1, dim, 10);
    }
  }

  public void testFew() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(.3, .3), 3, dim, 7);
      _checkCenter(Tensors.vector(.1, .3), 3, dim, 7);
      _checkCenter(Tensors.vector(5, 4.3), 3, dim, 11);
      _checkCenter(Tensors.vector(5, -3.3), 3, dim, 11);
    }
  }

  public void testMany() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(.3, .3), 20, dim, 8);
      _checkCenter(Tensors.vector(.1, .3), 20, dim, 8);
      _checkCenter(Tensors.vector(5, 4.3), 20, dim, 12);
      _checkCenter(Tensors.vector(5, -3.3), 20, dim, 12);
    }
  }

  public void testMost() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(.3, .3), 60, dim, 9);
      _checkCenter(Tensors.vector(.1, .3), 60, dim, 9);
      _checkCenter(Tensors.vector(5, 4.3), 60, dim, 13);
      _checkCenter(Tensors.vector(5, -3.3), 60, dim, 13);
    }
  }

  public void testAll() {
    for (int dim = 1; dim < 5; ++dim) {
      _checkCenter(Tensors.vector(.3, .3), 160, dim, 10);
      _checkCenter(Tensors.vector(.1, .3), 160, dim, 10);
      _checkCenter(Tensors.vector(5, 4.3), 160, dim, 14);
      _checkCenter(Tensors.vector(5, -3.3), 160, dim, 14);
    }
  }
}
