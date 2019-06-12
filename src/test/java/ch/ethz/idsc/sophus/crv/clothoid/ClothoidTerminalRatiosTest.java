// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class ClothoidTerminalRatiosTest extends TestCase {
  public void testLeft() {
    ClothoidTerminalRatios clothoidTerminalRatios = new ClothoidTerminalRatios( //
        Tensors.vector(0, 1, 0), Tensors.vector(2, 2, 0), 3);
    // turn left
    Chop._10.requireClose(clothoidTerminalRatios.head(), RealScalar.of(+0.9068461106738649));
    // turn right
    Chop._10.requireClose(clothoidTerminalRatios.tail(), RealScalar.of(-0.9068461106738649));
  }

  public void testLeftUniv() {
    ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of( //
        Tensors.vector(0, 1, 0), Tensors.vector(2, 2, 0));
    // turn left
    Chop._08.requireClose(clothoidTerminalRatios.head(), RealScalar.of(+1.2190137723033907));
    // turn right
    Chop._08.requireClose(clothoidTerminalRatios.tail(), RealScalar.of(-1.2190137715979599));
  }

  public void testRight() {
    ClothoidTerminalRatios clothoidTerminalRatios = new ClothoidTerminalRatios( //
        Tensors.vector(0, 1, 0), Tensors.vector(2, 0, 0), 3);
    // turn right
    Chop._10.requireClose(clothoidTerminalRatios.head(), RealScalar.of(-0.9068461106738649));
    // turn left
    Chop._10.requireClose(clothoidTerminalRatios.tail(), RealScalar.of(+0.9068461106738649));
  }

  public void testLeftUnit() {
    ClothoidTerminalRatios clothoidTerminalRatios = new ClothoidTerminalRatios( //
        Tensors.fromString("{0[m], 1[m], 0}"), Tensors.fromString("{2[m], 2[m], 0}"), 3);
    // turn left
    Chop._10.requireClose(clothoidTerminalRatios.head(), Quantity.of(+0.9068461106738649, "m^-1"));
    // turn right
    Chop._10.requireClose(clothoidTerminalRatios.tail(), Quantity.of(-0.9068461106738649, "m^-1"));
  }

  public void testCurve() {
    Distribution distribution = NormalDistribution.standard();
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, 1);
    for (int depth = 2; depth < 5; ++depth)
      for (int count = 0; count < 10; ++count) {
        Tensor beg = RandomVariate.of(distribution, 3);
        Tensor end = RandomVariate.of(distribution, 3);
        Tensor init = Tensors.of(beg, end);
        Tensor curve = Nest.of(curveSubdivision::string, init, depth);
        Scalar head = ClothoidTerminalRatios.curvature(curve.extract(0, 3));
        Scalar tail = ClothoidTerminalRatios.curvature(curve.extract(curve.length() - 3, curve.length()));
        ClothoidTerminalRatios clothoidTerminalRatios = new ClothoidTerminalRatios(beg, end, depth);
        assertEquals(head, clothoidTerminalRatios.head());
        assertEquals(tail, clothoidTerminalRatios.tail());
      }
  }

  public void testPercision() {
    TableBuilder tableBuilder = new TableBuilder();
    for (int depth = 5; depth < ClothoidTerminalRatios.MAX_ITER; ++depth) {
      ClothoidTerminalRatios clothoidTerminalRatios = //
          new ClothoidTerminalRatios(Tensors.vector(0, 1, 0), Tensors.vector(2, 0, 0), depth);
      tableBuilder.appendRow(RealScalar.of(depth), clothoidTerminalRatios.head().map(Round._8), clothoidTerminalRatios.tail().map(Round._8));
    }
    // System.out.println(MatrixForm.of(tableBuilder.toTable()));
  }

  private static void _checkZero(Tensor pose, Scalar zero) {
    {
      ClothoidTerminalRatios clothoidTerminalRatios = new ClothoidTerminalRatios(pose, pose, 3);
      assertEquals(clothoidTerminalRatios.head(), zero);
      assertEquals(clothoidTerminalRatios.tail(), zero);
    }
    {
      ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(pose, pose);
      assertEquals(clothoidTerminalRatios.head(), zero);
      assertEquals(clothoidTerminalRatios.tail(), zero);
    }
  }

  public void testSame() {
    _checkZero(Tensors.vector(1, 1, 1), RealScalar.ZERO);
    _checkZero(Tensors.fromString("{1[m], 1[m], 1}"), Quantity.of(0, "m^-1"));
    _checkZero(RandomVariate.of(UniformDistribution.unit(), 3), Quantity.of(0, ""));
    _checkZero(Tensors.fromString("{1.1[m], 1.2[m], 1.3}"), Quantity.of(0, "m^-1"));
  }

  public void testOpenEnd() {
    Distribution distribution = NormalDistribution.standard();
    Chop chop = Chop._02;
    int failCount = 0;
    for (int count = 0; count < 20; ++count) {
      Tensor beg = RandomVariate.of(distribution, 3);
      Tensor end = RandomVariate.of(distribution, 3);
      ClothoidTerminalRatios clothoidTerminalRatios1 = ClothoidTerminalRatios.of(beg, end);
      ClothoidTerminalRatios clothoidTerminalRatios2 = new ClothoidTerminalRatios(beg, end, ClothoidTerminalRatios.MAX_ITER);
      if (!chop.close(clothoidTerminalRatios1.head(), clothoidTerminalRatios2.head())) {
        // beg={0.33199331585891245, -0.553240463025886, -0.03881900926835866}
        // end={-1.3174375242633647, -0.9411502957371748, 0.25948643363292373}
        System.out.println("beg=" + beg);
        System.out.println("end=" + end);
        Scalar err = clothoidTerminalRatios1.head().subtract(clothoidTerminalRatios2.head()).abs();
        System.out.println("err=" + err);
        // chop.requireClose(clothoidTerminalRatios1.head(), clothoidTerminalRatios2.head());
        ++failCount;
      }
    }
    if (10 < failCount)
      fail();
  }

  public void testOpenEndUnit() {
    ClothoidTerminalRatios clothoidTerminalRatios1 = ClothoidTerminalRatios.of( //
        Tensors.fromString("{1[m], 1[m], 1}"), //
        Tensors.fromString("{2[m], 3[m], 3}"));
    ClothoidTerminalRatios clothoidTerminalRatios2 = new ClothoidTerminalRatios( //
        Tensors.fromString("{1[m], 1[m], 1}"), //
        Tensors.fromString("{2[m], 3[m], 3}"), ClothoidTerminalRatios.MAX_ITER);
    ClothoidTerminalRatios.CHOP.requireClose(clothoidTerminalRatios1.head(), clothoidTerminalRatios2.head());
  }

  public void testMaxIter() {
    assertTrue(ClothoidTerminalRatios.MAX_ITER <= 20);
  }

  public void testDepthZeroFail() {
    try {
      new ClothoidTerminalRatios(Tensors.vector(0, 1, 0), Tensors.vector(2, 0, 0), 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
