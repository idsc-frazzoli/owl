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
    Assert.assertEquals(control, actual);
  }

  public void testUniform() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,0,0},{4,4,0,0},{5,5,0,0},{6,6,0,0}}");
    Scalar radius = RealScalar.of(1.1);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Assert.assertEquals(control.extract(1, control.length() - 1), actual.extract(1, control.length() - 1));
  }

  public void testSimple() {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2.5,2,0,0},{3,3,0,0},{3.5,4,0,0},{5,5,0,0},{8,6,0,0}}");
    Scalar radius = RealScalar.of(1.1);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, radius, SmoothingKernel.GAUSSIAN);
    TensorUnaryOperator nonuniformGeodesicCenterFilter = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, radius);
    // ---
    Tensor actual = nonuniformGeodesicCenterFilter.apply(control);
    Tensor expected = Tensors.fromString(
        "{{0, 0.620441730433741, 0.0, 0.0}, {1, 0.3795582695662591, 0.0, 0.0}, {2.5, 3.362955220228585, 0.0, 0.0}, {3, 3.0, 0.0, 0.0}, {3.5, 2.7253868427387706, 0.0, 0.0}, {5, 5, 0, 0}, {8, 6, 0, 0}}");
    Assert.assertEquals(expected, actual);
  }

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
        "{{0, 1.009972463191999, 0.8039819375934649, 0.9219753325835081}, {0.9814572945363066, 1.3503914029959625, 0.7756729634953909, 1.3152876273508858}, {1.4366908384419668, 1.1543621266804203, 0.5012477998371321, 1.1262330030480892}, {2.2312370773882813, 2.5179200296796607, 1.3266487503899747, 2.41428189520095}, {3.078925714851043, 2.950959844088803, 2.6720523335461257, 3.368252276747113}, {3.557906539505889, 2.906461894159947, 2.5374526150734917, 3.2730292500876654}, {3.712706090440031, 2.6636724962278904, 2.4743710053831824, 3.1561734579273795}, {4.3527164352047825, 2.5444755071122063, 2.773179717521184, 3.3918743046809534}}");
    Assert.assertEquals(expected, actual);
  }
}