// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ abstract class A2KrigingDemo extends A1KrigingDemo {
  final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  final JToggleButton jToggleButton = new JToggleButton("thres");

  public A2KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(geodesicDisplay);
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100);
      spinnerRes.setIndex(0);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
    }
    {
      timerFrame.jToolBar.add(jToggleButton);
    }
    {
      JButton jButton = new JButton("round");
      jButton.addActionListener(e -> {
        Tensor tensor = getControlPointsSe2().copy();
        tensor.set(Round.FUNCTION, Tensor.ALL, 2);
        setControlPointsSe2(tensor);
      });
      timerFrame.jToolBar.add(jButton);
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame.configCoordinateOffset(100, 700);
  }
}
