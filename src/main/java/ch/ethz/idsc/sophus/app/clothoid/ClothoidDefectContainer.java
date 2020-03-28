package ch.ethz.idsc.sophus.app.clothoid;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidContext;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTangentDefect;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/* package */ class ClothoidDefectContainer {
  public static final Tensor LAMBDAS = Subdivide.of(-20.0, 20.0, 1001);
  // ---
  public final ClothoidContext clothoidContext;
  private final ClothoidTangentDefect clothoidTangentDefect;
  public final Tensor defects;
  public final JFreeChart jFreeChart;

  public ClothoidDefectContainer(ClothoidContext clothoidContext) {
    this.clothoidContext = clothoidContext;
    clothoidTangentDefect = ClothoidTangentDefect.of(clothoidContext.s1(), clothoidContext.s2());
    defects = LAMBDAS.map(clothoidTangentDefect);
    VisualSet visualSet = new VisualSet();
    visualSet.add(LAMBDAS, defects.map(Real.FUNCTION));
    visualSet.add(LAMBDAS, defects.map(Imag.FUNCTION));
    jFreeChart = ListPlot.of(visualSet);
  }

  public boolean encodes(ClothoidContext clothoidContext) {
    return this.clothoidContext.s1().equals(clothoidContext.s1()) //
        && this.clothoidContext.s2().equals(clothoidContext.s2());
  }
}
