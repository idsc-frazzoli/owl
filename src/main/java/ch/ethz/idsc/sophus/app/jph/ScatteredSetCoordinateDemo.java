// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ abstract class ScatteredSetCoordinateDemo extends ControlPointsDemo {
  final SpinnerLabel<Supplier<BarycentricCoordinate>> spinnerBarycentric = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerMagnif = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  // final JToggleButton jToggleMidpoints = new JToggleButton("midp.");
  final JToggleButton jToggleHeatmap = new JToggleButton("heatmap");
  final JToggleButton jToggleArrows = new JToggleButton("arrows");

  public ScatteredSetCoordinateDemo(boolean addRemoveControlPoints, List<GeodesicDisplay> list, Supplier<BarycentricCoordinate>[] array) {
    super(addRemoveControlPoints, list);
    {
      spinnerBarycentric.setArray(array);
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    {
      spinnerRefine.setList(Arrays.asList(3, 5, 10, 15, 20, 25, 30, 35, 40, 50));
      spinnerRefine.setValue(20);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
    {
      spinnerMagnif.setList(Arrays.asList(1, 2, 3, 4));
      spinnerMagnif.setValue(2);
      spinnerMagnif.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
    }
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setIndex(0);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    }
    {
      // jToggleMidpoints.setSelected(isMidpointIndicated());
      // jToggleMidpoints.addActionListener(l -> setMidpointIndicated(jToggleMidpoints.isSelected()));
      // timerFrame.jToolBar.add(jToggleMidpoints);
      jToggleHeatmap.setSelected(true);
      timerFrame.jToolBar.add(jToggleHeatmap);
      jToggleArrows.setSelected(false);
      timerFrame.jToolBar.add(jToggleArrows);
    }
    timerFrame.jToolBar.addSeparator();
    setMidpointIndicated(false);
  }

  final int refinement() {
    return spinnerRefine.getValue();
  }

  final int magnification() {
    return spinnerMagnif.getValue();
  }

  final ColorDataGradient colorDataGradient() {
    return spinnerColorData.getValue();
  }

  final BarycentricCoordinate barycentricCoordinate() {
    return spinnerBarycentric.getValue().get();
  }
}
