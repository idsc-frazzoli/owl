// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.Assert;
import junit.framework.TestCase;

public class NonuniformGeodesicCenterFilterTest extends TestCase {
  public void testSmallInterval() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,0,0},{4,4,0,0},{5,5,0,0},{6,6,0,0}}");
    Scalar radius = RealScalar.of(0.9);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Tensor expected = Tensor.of(control.stream().map(st -> st.extract(1, 4)));
    Assert.assertEquals(expected, actual);
  }

  public void testUniform() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,0,0},{4,4,0,0},{5,5,0,0},{6,6,0,0}}");
    Scalar radius = RealScalar.of(1.1);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Tensor expected = Tensor.of(control.stream().map(st -> st.extract(1, 4)));
    Assert.assertEquals(expected.extract(1, control.length() - 1), actual.extract(1, control.length() - 1));
  }

  // Not sure if this is correct
  public void testSimple() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2.5,2,0,0},{3,3,0,0},{3.5,4,0,0},{5,5,0,0},{8,6,0,0}}");
    Scalar radius = RealScalar.of(1.1);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Tensor expected = Tensors.fromString(
        "{{0.3795582695662591, 0.0, 0.0}, {0.620441730433741, 0.0, 0.0}, {2.4064812563435085, 0.0, 0.0}, {3.0, 0.0, 0.0}, {3.5935187436564915, 0.0, 0.0}, {5, 0, 0}, {6, 0, 0}}");
    Assert.assertEquals(expected, actual);
  }

  // Not sure if this is correct
  public void testSimple2() {
    // randomly created control sequence
    Tensor control = Tensors.fromString(
        "{{0, 0, 0, 0}, {0.9814572945363066, 0.6695271477459349, 0.5841504981126208, 0.9559736678490417}, {1.4366908384419668, 1.2270683341270914, 1.4949861951816952, 1.3512273634641077}, {2.2312370773882813, 1.3468786522912515, 1.8307626209367944, 2.3078747864481706}, {3.078925714851043, 1.8133743890403036, 2.26911308781397, 2.8269600949484}, {3.557906539505889, 2.199350646730907, 3.030947337282967, 3.5143002293067918}, {3.712706090440031, 2.751427965131654, 3.0466280461277986, 3.813553735932098}, {4.3527164352047825, 3.7043433716235743, 3.930508556443228, 3.9600296399227757}}");
    Scalar radius = RealScalar.of(1.5);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Tensor expected = Tensors.fromString(
        "{{0.41547382820122997, 0.16542110578224065, 0.3283719937349692}, {0.9208982706735835, 0.25553680754541663, 0.7613679582215839}, {1.3742649618778098, 0.8497391884751797, 1.4696557920686992}, {2.191717995942458, 1.2548806412210087, 2.18760439584916}, {2.133022234932061, 1.99670714013345, 2.7802021508800427}, {2.709240669957797, 2.332207589285892, 3.106491898993758}, {3.3260660608553088, 3.087724892448798, 3.553410896025524}, {3.5133257625597034, 3.628263413168984, 3.8376339536224067}}");
    Assert.assertEquals(expected, actual);
  }
}