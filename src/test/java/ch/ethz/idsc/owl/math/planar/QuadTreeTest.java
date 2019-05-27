// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdEntry;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class QuadTreeTest extends TestCase {
  private static final Tensor POINTS = Tensors.of( //
      Tensors.fromString("{100,200,0}"), //
      Tensors.fromString("{101,201,1}"), //
      Tensors.fromString("{101,202,2}"), //
      Tensors.fromString("{101,203,3}"), //
      Tensors.fromString("{101,204,4}"), //
      Tensors.fromString("{101,205,5}"), //
      Tensors.fromString("{101,206,6}"), //
      Tensors.fromString("{101,207,7}"), //
      Tensors.fromString("{101,208,8}"), //
      Tensors.fromString("{101,209,9}"), //
      Tensors.fromString("{110,210,10}")).unmodifiable();

  public void testSimple() {
    QuadTree quadTree = new QuadTree(Clips.interval(100, 110), Clips.interval(200, 210), 2);
    POINTS.stream().forEach(quadTree::insert);
    assertEquals(POINTS.get(5), quadTree.closest(Tensors.fromString("{103,205,999}")).get());
  }

  public void testUnits() {
    QuadTree quadTree = new QuadTree( //
        Clips.interval(Quantity.of(100, "m"), Quantity.of(110, "m")), //
        Clips.interval(Quantity.of(200, "m"), Quantity.of(210, "m")), 2);
    Tensor points = POINTS.copy();
    points.forEach(point -> point.set(Quantity.of(point.Get(0), "m"), 0));
    points.forEach(point -> point.set(Quantity.of(point.Get(1), "m"), 1));
    points.stream().forEach(quadTree::insert);
    assertEquals(points.get(5), quadTree.closest(Tensors.fromString("{103[m],205[m],999}")).get());
  }

  public void testNdTreeMap() {
    Distribution dX = DiscreteUniformDistribution.of(0, 1000);
    Distribution dY = DiscreteUniformDistribution.of(0, 2000);
    for (int attempt = 0; attempt < 100; ++attempt) {
      NdTreeMap<Scalar> ndTreeMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(1, 2), 2, 5);
      QuadTree quadTree = new QuadTree(Clips.unit(), Clips.interval(0, 2), 3);
      for (int count = 0; count < 100; ++count) {
        Tensor key = Tensors.of(RandomVariate.of(dX), RandomVariate.of(dY)).divide(RealScalar.of(1000));
        Scalar value = RealScalar.of(count);
        ndTreeMap.add(key, value);
        quadTree.insert(key.copy().append(value));
      }
      final Tensor ref = Tensors.fromString("{1/3, 4/3}").unmodifiable();
      Optional<Tensor> closest = quadTree.closest(ref.copy().append(RealScalar.ZERO));
      final Tensor tensor = closest.get();
      // ---
      NdCluster<Scalar> buildCluster = ndTreeMap.buildCluster(NdCenterInterface.euclidean(ref), 1);
      NdEntry<Scalar> ndEntry = buildCluster.collection().iterator().next();
      if (tensor.extract(0, 2).equals(ndEntry.location())) {
        assertEquals(tensor.extract(0, 2), ndEntry.location());
        assertEquals(tensor.Get(2), ndEntry.value());
      } else {
        System.out.println("nd=" + ndEntry.distance());
        System.out.println("qt=" + Norm._2.between(tensor.extract(0, 2), ref));
        System.out.println("---");
      }
    }
  }
}
