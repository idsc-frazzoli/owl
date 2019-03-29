// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicAverage;
import ch.ethz.idsc.sophus.filter.GeodesicAverageFilter;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.sophus.sym.SymWeightsToSplits;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.VectorTotal;

public class GeodesicAverageFilterDemo extends DatasetKernelDemo {
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();
  private Tensor refined = Tensors.empty();

  public GeodesicAverageFilterDemo() {
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
    
    // TODO OB: Think of a way to create different trees automatically?
    Tensor tree = Tensors.of(Tensors.vector(0, 1), Tensors.of(Tensors.vector(2, 3), RealScalar.of(4)));
    Tensor weights = Normalize.with(VectorTotal.FUNCTION).apply(Tensors.vector(1, 2, 3, 2, 1));
    
    SymWeightsToSplits symWeightsToSplits = new SymWeightsToSplits(weights);
    
    TensorUnaryOperator tensorUnaryOperator = GeodesicAverage.of(geodesicDisplay().geodesicInterface(), symWeightsToSplits.recursion(tree));
    refined = GeodesicAverageFilter.of(tensorUnaryOperator, weights.length()).apply(control());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
//    TODO OB: display new splits
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicAverageFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
