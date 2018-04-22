// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Tally;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class NdTreeMapTest extends TestCase {
  public void testSome() {
    NdMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    ndTreeMap.add(Tensors.vector(1, 1), "d1");
    ndTreeMap.add(Tensors.vector(1, 0), "d2");
    ndTreeMap.add(Tensors.vector(0, 1), "d3");
    ndTreeMap.add(Tensors.vector(1, 1), "d4");
    ndTreeMap.add(Tensors.vector(0.1, 0.1), "d5");
    ndTreeMap.add(Tensors.vector(6, 7), "d6");
    {
      Tensor center = Tensors.vector(0, 0);
      NdCenterInterface distancer = NdCenterInterface.euclidean(center);
      NdCluster<String> cluster = ndTreeMap.buildCluster(distancer, 1);
      assertTrue(cluster.collection().iterator().next().value().equals("d5"));
    }
    {
      Tensor center = Tensors.vector(5, 5);
      NdCenterInterface distancer = NdCenterInterface.euclidean(center);
      NdCluster<String> cluster = ndTreeMap.buildCluster(distancer, 1);
      assertTrue(cluster.collection().iterator().next().value().equals("d6"));
    }
    {
      Tensor center = Tensors.vector(1.1, 0.9);
      NdCenterInterface distancer = NdCenterInterface.euclidean(center);
      NdCluster<String> cluster = ndTreeMap.buildCluster(distancer, 2);
      assertEquals(cluster.size(), 2);
      List<String> list = Arrays.asList("d1", "d4");
      for (NdEntry<String> point : cluster.collection())
        assertTrue(list.contains(point.value()));
    }
  }

  public void testEmpty() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    assertTrue(ndMap.isEmpty());
    NdCenterInterface distancer = NdCenterInterface.euclidean(Tensors.vector(0, 0));
    NdCluster<String> cluster = ndMap.buildCluster(distancer, 2);
    assertEquals(cluster.size(), 0);
  }

  public void testClear() throws Exception {
    NdMap<String> ndMap = new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 10);
    ndMap.add(Tensors.vector(1, 1), "d1");
    ndMap.add(Tensors.vector(1, 0), "d2");
    ndMap.add(Tensors.vector(0, 1), "d3");
    NdCenterInterface ndCenter = NdCenterInterface.euclidean(Tensors.vector(0, 0));
    {
      NdCluster<String> cluster = ndMap.buildCluster(ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
    NdMap<String> ndMap2 = Serialization.copy(ndMap);
    {
      ndMap.clear();
      NdCluster<String> cluster = ndMap.buildCluster(ndCenter, 5);
      assertEquals(cluster.size(), 0);
    }
    ndMap.clear();
    {
      NdCluster<String> cluster = ndMap2.buildCluster(ndCenter, 5);
      assertEquals(cluster.size(), 3);
    }
  }

  public void testCornerCase() {
    NdMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), 10, 2);
    Tensor location = Array.zeros(2);
    for (int c = 0; c < 400; ++c)
      ndTreeMap.add(location, "s" + c);
  }

  public void testSimple1() {
    final int n = 10;
    NdTreeMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), n, 26);
    for (int c = 0; c < 800; ++c)
      ndTreeMap.add(RandomVariate.of(UniformDistribution.unit(), 2), "s" + c);
    Tensor flatten = Flatten.of(ndTreeMap.binSize());
    assertEquals(Total.of(flatten), RealScalar.of(800));
    NavigableMap<Tensor, Long> map = Tally.sorted(flatten);
    Tensor last = map.lastKey();
    assertEquals(last, RealScalar.of(n));
  }

  public void testPrint() {
    NdMap<String> ndTreeMap = //
        new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 1), 3, 3);
    for (int c = 0; c < 12; ++c) {
      Tensor location = RandomVariate.of(UniformDistribution.unit(), 2);
      ndTreeMap.add(location, "s" + c);
    }
    // testTree.print();
    // System.out.println(testTree.binSize());
  }

  public void testFail0() {
    try {
      new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3), 2, 2);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail1() {
    NdMap<String> ndTreeMap = new NdTreeMap<>( //
        Tensors.vector(-2, -3), Tensors.vector(8, 9), 2, 2);
    Tensor location = Array.zeros(3);
    try {
      ndTreeMap.add(location, "string");
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail2() {
    try {
      new NdTreeMap<>(Tensors.vector(-2, 10), Tensors.vector(8, 9), 10, 10);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
