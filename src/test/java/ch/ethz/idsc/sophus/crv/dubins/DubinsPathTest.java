// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.dubins.DubinsPath.Type;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DubinsPathTest extends TestCase {
  public void testFirstTurnRight() {
    assertFalse(Type.LSR.isFirstTurnRight());
    assertTrue(Type.RSL.isFirstTurnRight());
    assertFalse(Type.LSL.isFirstTurnRight());
    assertTrue(Type.RSR.isFirstTurnRight());
    assertFalse(Type.LRL.isFirstTurnRight());
    assertTrue(Type.RLR.isFirstTurnRight());
  }

  public void testFirstEqualsLast() {
    assertFalse(Type.LSR.isFirstEqualsLast());
    assertFalse(Type.RSL.isFirstEqualsLast());
    assertTrue(Type.LSL.isFirstEqualsLast());
    assertTrue(Type.RSR.isFirstEqualsLast());
    assertTrue(Type.LRL.isFirstEqualsLast());
    assertTrue(Type.RLR.isFirstEqualsLast());
  }

  public void testTangentUnit() {
    Tensor tensor = Type.LSR.tangent(2, Quantity.of(10, "m"));
    assertEquals(tensor.get(0), RealScalar.ONE);
    assertEquals(tensor.get(1), RealScalar.ZERO);
    assertEquals(tensor.get(2), Quantity.of(RationalScalar.of(-1, 10), "m^-1"));
  }

  public void testTangentDimensionless() {
    Tensor tensor = Type.LSR.tangent(2, RealScalar.of(10));
    assertEquals(tensor.get(0), RealScalar.ONE);
    assertEquals(tensor.get(1), RealScalar.ZERO);
    assertEquals(tensor.get(2), RationalScalar.of(-1, 10));
  }

  public void testSignatureAbs() {
    assertEquals(Type.LSL.signatureAbs(), Type.LSR.signatureAbs());
    assertEquals(Type.LSL.signatureAbs(), Type.LSR.signatureAbs());
    assertEquals(Type.LSL.signatureAbs(), Type.RSR.signatureAbs());
    assertEquals(Type.LSL.signatureAbs(), Type.RSL.signatureAbs());
    assertEquals(Type.LSL.signatureAbs(), Tensors.vector(1, 0, 1));
    assertEquals(Type.LRL.signatureAbs(), Type.RLR.signatureAbs());
    assertEquals(Type.LRL.signatureAbs(), Tensors.vector(1, 1, 1));
  }

  public void testFail() {
    try {
      Type.LSR.tangent(2, RealScalar.of(-10));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testAnticipateFail() {
    try {
      Type.LSR.tangent(-2, RealScalar.of(10));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testWithoutUnits() {
    DubinsPath dubinsPath = new DubinsPath(Type.LSR, RealScalar.of(2), Tensors.vector(3, 2, 1));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.vector(0, 0, 0));
    Chop._10.requireClose(scalarTensorFunction.apply(RealScalar.of(0.3)), //
        Tensors.fromString("{0.29887626494719843, 0.022457844127915516, 0.15}"));
    Chop._10.requireClose(scalarTensorFunction.apply(RealScalar.of(4.7)), //
        Tensors.fromString("{2.1152432160432038, 3.554267073891487, 1.5}"));
    Chop._10.requireClose(scalarTensorFunction.apply(RealScalar.of(5.8)), //
        Tensors.fromString("{2.349039629628753, 4.6192334093884515, 1.1}"));
  }

  public void testUnits() {
    DubinsPath dubinsPath = new DubinsPath(Type.LRL, Quantity.of(2, "m"), Tensors.fromString("{1[m], 10[m], 1[m]}"));
    assertEquals(dubinsPath.length(), Quantity.of(12, "m"));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.fromString("{1[m], 2[m], 3}"));
    Tensor tensor = scalarTensorFunction.apply(Quantity.of(0.3, "m"));
    Chop._10.requireClose(tensor, Tensors.fromString("{0.7009454891459682[m], 2.0199443237417927[m], 3.15}"));
  }

  public void testMemberFuncs() {
    DubinsPath dubinsPath = new DubinsPath(Type.LRL, Quantity.of(2, "m"), Tensors.fromString("{1[m], 10[m], 1[m]}"));
    assertEquals(dubinsPath.type(), Type.LRL);
    Scalar curvature = dubinsPath.totalCurvature();
    ExactScalarQ.require(curvature);
    assertEquals(curvature, RealScalar.of(6));
  }

  public void testStraight() {
    DubinsPath dubinsPath = new DubinsPath(Type.LSL, Quantity.of(2, "m"), Tensors.fromString("{0[m], 10[m], 0[m]}"));
    assertEquals(dubinsPath.totalCurvature(), RealScalar.ZERO);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    DubinsPath path = new DubinsPath(Type.LRL, Quantity.of(2, "m"), Tensors.fromString("{1[m], 8[m], 1[m]}"));
    DubinsPath dubinsPath = Serialization.copy(path);
    assertEquals(dubinsPath.type(), Type.LRL);
    assertEquals(dubinsPath.length(), Quantity.of(10, "m"));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.fromString("{1[m], 2[m], 3}"));
    Tensor tensor = scalarTensorFunction.apply(Quantity.of(0.3, "m"));
    Chop._10.requireClose(tensor, Tensors.fromString("{0.7009454891459682[m], 2.0199443237417927[m], 3.15}"));
  }

  public void testSignFail() {
    try {
      new DubinsPath(Type.LRL, RealScalar.ONE, Tensors.vector(1, -10, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testOutsideFail() {
    DubinsPath dubinsPath = new DubinsPath(Type.LRL, RealScalar.ONE, Tensors.vector(1, 10, 1));
    assertEquals(dubinsPath.length(), Quantity.of(12, ""));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(Tensors.vector(1, 2, 3));
    try {
      scalarTensorFunction.apply(RealScalar.of(-.1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      scalarTensorFunction.apply(RealScalar.of(13));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      new DubinsPath(null, RealScalar.ONE, Tensors.vector(1, 10, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testVectorFail() {
    try {
      new DubinsPath(Type.RLR, RealScalar.ONE, Tensors.vector(1, 10, 1, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
