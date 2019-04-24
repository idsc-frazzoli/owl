package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformCenterFilterTest extends TestCase {
  public void testSmallInterval() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,3,0},{3.5,4,5,0},{4,6,2,0},{5,3,3,0},{7,9,2,0}}");
    NonuniformCenterFilter nonuniformCenterFilter = new NonuniformCenterFilter(Se2Geodesic.INSTANCE, RealScalar.of(0.2), control);
    Tensor result = Tensors.empty();
    for (int i = 0; i < control.length(); ++i) {
      Tensor state = control.get(i);
      Tensor extracted = nonuniformCenterFilter.selection(state);
      Tensor splits = nonuniformCenterFilter.splits(extracted, state);
      result.append(nonuniformCenterFilter.apply(splits, extracted, state));
    }
    Assert.assertEquals(control, result);
  }
  
  public void testSimple() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,3,0},{3.5,4,5,0},{4,6,2,0},{5,3,3,0},{7,9,2,0}}");
    NonuniformCenterFilter nonuniformCenterFilter = new NonuniformCenterFilter(Se2Geodesic.INSTANCE, RealScalar.of(0.6), control);
    Tensor result = Tensors.empty();
    for (int i = 0; i < control.length(); ++i) {
      Tensor state = control.get(i);
      Tensor extracted = nonuniformCenterFilter.selection(state);
      Tensor splits = nonuniformCenterFilter.splits(extracted, state);
      result.append(nonuniformCenterFilter.apply(splits, extracted, state));
    }
    Tensor expected = Tensors.fromString("{{0, 0.0, 0.0, 0.0}, {1, 1.0, 0.0, 0.0}, {2, 2.0, 0.0, 0.0}, {3, 3.637988445859089, 4.275976891718178, 0.0}, {3.5, 4.5, 2.5, 0.0}, {4, 4.724023108281822, 3.9139653375772667, 0.0}, {5, 3.0, 3.0, 0.0}, {7, 9.0, 2.0, 0.0}}");
    Assert.assertEquals(expected, result);
  }
  
  public void testSimple2() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,3,0},{3.5,4,5,0},{4,6,2,0},{5,3,3,0},{7,9,2,0}}");
    NonuniformCenterFilter nonuniformCenterFilter = new NonuniformCenterFilter(Se2Geodesic.INSTANCE, RealScalar.of(2), control);
    Tensor result = Tensors.empty();
    for (int i = 0; i < control.length(); ++i) {
      Tensor state = control.get(i);
      Tensor extracted = nonuniformCenterFilter.selection(state);
      Tensor splits = nonuniformCenterFilter.splits(extracted, state);
      result.append(nonuniformCenterFilter.apply(splits, extracted, state));
    }
    System.err.println(result);
    Tensor expected = Tensors.fromString("{{0, 1.3612720358129318, 0.0, 0.0}, {1, 1.5468240896250542, 1.3595277311248366, 0.0}, {2, 3.062251349645987, 1.6174444940000423, 0.0}, {3, 2.988846818148774, 1.8048793354957702, 0.0}, {3.5, 2.999682978785304, 1.7779572492483304, 0.0}, {4, 2.8726079168177128, 2.4781395977865937, 0.0}, {5, 5.5168206000631645, 3.0615594792920673, 0.0}, {7, 5.401244404038803, 2.5997925993268662, 0.0}}");
    Assert.assertEquals(expected, result);
  }
}
