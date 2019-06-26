// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.flt.ga.GeodesicAverage;
import ch.ethz.idsc.sophus.flt.ga.GeodesicAverageFilter;
import ch.ethz.idsc.sophus.math.NormalizeTotal;
import ch.ethz.idsc.sophus.sym.SymWeightsToSplits;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH OWL 045 possibly remove
/* package */ class GeodesicAverageFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public GeodesicAverageFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
    // TODO OB extract functionality below to separate function/class where it can be documented and tested
    // left seeded tree
    Tensor tree = Tensors.vector(0, 1);
    for (int index = 2; index <= spinnerRadius.getValue(); ++index) {
      tree = Tensors.of(tree, RealScalar.of(index));
    }
    // right seeded tree
    Tensor tree2 = Tensors.vector(spinnerRadius.getValue() - 1, spinnerRadius.getValue());
    for (int index = spinnerRadius.getValue(); index >= 2; --index) {
      tree2 = Tensors.of(RealScalar.of(index - 2), tree2);
    }
    // balanced tree
    Tensor tree3 = Tensors.empty();
    Tensor subtree3 = Tensors.empty();
    for (int index = 0; index < spinnerRadius.getValue(); ++index)
      tree3.append(Tensors.vector(index, index + 1));
    for (int outer = 1; outer < spinnerRadius.getValue(); ++outer) {
      for (int inner = 0; inner < spinnerRadius.getValue() - outer; ++inner) {
        subtree3.append(Tensors.of(tree3.get(inner), tree3.get(inner + 1)));
      }
      tree3 = subtree3;
      subtree3 = Tensors.empty();
    }
    tree3 = tree3.get(0);
    // ---
    Tensor weights = Tensors.empty();
    for (int index = 0; index < spinnerRadius.getValue() + 1; ++index)
      weights.append(spinnerKernel.getValue().apply( //
          RealScalar.of(index).divide(RealScalar.of(spinnerRadius.getValue())).subtract(RealScalar.of(0.5))));
    weights = NormalizeTotal.FUNCTION.apply(weights);
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(tree, weights);
    System.out.println(symWeightsToSplits.splits());
    TensorUnaryOperator tensorUnaryOperator = GeodesicAverage.of(geodesicDisplay().geodesicInterface(), symWeightsToSplits.splits());
    refined = GeodesicAverageFilter.of(tensorUnaryOperator, weights.length()).apply(control());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO OB display new splits
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicAverageFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
