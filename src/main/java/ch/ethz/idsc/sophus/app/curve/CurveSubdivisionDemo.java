// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.app.util.StandardMenu;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ abstract class CurveSubdivisionDemo extends CurvatureDemo {
  final SpinnerLabel<CurveSubdivisionSchemes> spinnerLabel = new SpinnerLabel<>();
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  final SpinnerLabel<Scalar> spinnerMagicC = new SpinnerLabel<>();
  final JToggleButton jToggleLine = new JToggleButton("line");
  final JToggleButton jToggleCyclic = new JToggleButton("cyclic");
  final JToggleButton jToggleSymi = new JToggleButton("graph");

  public CurveSubdivisionDemo(List<GeodesicDisplay> _list) {
    super(_list);
    Tensor control = null;
    {
      Tensor move = Tensors.fromString( //
          "{{1, 0, 0}, {1,0,0}, {2,0,2.5708}, {1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
      move = Tensor.of(move.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))));
      Tensor init = Tensors.vector(0, 0, 2.1);
      control = DubinsGenerator.of(init, move);
      control = Tensors.fromString("{{0,0,0}, {1,0,0},{2,0,0},{3,1,0},{4,1,0},{5,0,0},{6,0,0},{7,0,0}}").multiply(RealScalar.of(2));
    }
    setControlPointsSe2(control);
    timerFrame.jToolBar.addSeparator();
    {
      JButton jButton = new JButton("load");
      List<String> list = Arrays.asList("ducttape/20180514.csv", "tires/20190116.csv", "tires/20190117.csv");
      Supplier<StandardMenu> supplier = () -> new StandardMenu() {
        @Override
        protected void design(JPopupMenu jPopupMenu) {
          for (String string : list) {
            JMenuItem jMenuItem = new JMenuItem(string);
            jMenuItem.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent actionEvent) {
                Tensor tensor = ResourceData.of("/dubilab/controlpoints/" + string);
                tensor = Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(0.5, 0.5, 1))));
                Tensor center = Mean.of(tensor);
                center.set(RealScalar.ZERO, 2);
                tensor = Tensor.of(tensor.stream().map(row -> row.subtract(center)));
                setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
                jToggleCyclic.setSelected(true);
                setControlPointsSe2(tensor);
              }
            });
            jPopupMenu.add(jMenuItem);
          }
        }
      };
      StandardMenu.bind(jButton, supplier);
      timerFrame.jToolBar.add(jButton);
    }
    // ---
    jToggleLine.setSelected(false);
    timerFrame.jToolBar.add(jToggleLine);
    // ---
    timerFrame.jToolBar.addSeparator();
    addButtonDubins();
    // ---
    // jToggleCyclic.setSelected(true);
    timerFrame.jToolBar.add(jToggleCyclic);
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    spinnerLabel.setArray(CurveSubdivisionSchemes.values());
    spinnerLabel.setIndex(9);
    spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "scheme");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    spinnerMagicC.addSpinnerListener(value -> CurveSubdivisionHelper.MAGIC_C = value);
    spinnerMagicC.setList( //
        Tensors.fromString("{1/100, 1/10, 1/8, 1/6, 1/4, 1/3, 1/2, 2/3, 9/10, 99/100}").stream() //
            .map(Scalar.class::cast) //
            .collect(Collectors.toList()));
    spinnerMagicC.setValue(RationalScalar.HALF);
    spinnerMagicC.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    {
      JSlider jSlider = new JSlider(1, 999, 500);
      jSlider.setPreferredSize(new Dimension(360, 28));
      jSlider.addChangeListener(changeEvent -> //
      CurveSubdivisionHelper.MAGIC_C = RationalScalar.of(jSlider.getValue(), 1000));
      timerFrame.jToolBar.add(jSlider);
    }
    timerFrame.configCoordinateOffset(100, 600);
  }
}
