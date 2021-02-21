// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.ethz.idsc.sophus.app.bdn.AbstractScatteredSetWeightingDemo;
import ch.ethz.idsc.sophus.gds.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.ArrayPlotRender;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;

public abstract class AbstractExportWeightingDemo extends AbstractScatteredSetWeightingDemo implements ActionListener {
  private static final int REFINEMENT = 120; // presentation 60
  private final JButton jButtonExport = new JButton("export");

  public AbstractExportWeightingDemo( //
      boolean addRemoveControlPoints, List<ManifoldDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    LogWeighting logWeighting = logWeighting();
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        manifoldDisplay().toString(), //
        logWeighting.toString());
    root.mkdirs();
    for (Biinvariant biinvariant : distinct()) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
          biinvariant, //
          manifoldDisplay().hsManifold(), //
          variogram(), //
          sequence);
      System.out.print("computing " + biinvariant);
      // ---
      ArrayPlotRender arrayPlotRender = arrayPlotRender(sequence, REFINEMENT, tensorUnaryOperator, 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        ImageIO.write(bufferedImage, "png", new File(root, biinvariant.toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
  }

  protected final ArrayPlotRender arrayPlotRender(Tensor sequence, int refinement, TensorUnaryOperator tensorUnaryOperator, int magnification) {
    GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay().geodesicArrayPlot();
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence.length());
    Tensor wgs = geodesicArrayPlot.raster(refinement, tensorUnaryOperator, fallback);
    return StaticHelper.arrayPlotFromTensor(wgs, magnification, logWeighting().equals(LogWeightings.DISTANCES), colorDataGradient());
  }

  private static List<Biinvariant> distinct() {
    return Arrays.asList( //
        MetricBiinvariant.EUCLIDEAN, // FIXME should be retrieved from bitype
        Biinvariants.LEVERAGES, //
        Biinvariants.GARDEN, //
        Biinvariants.HARBOR);
  }
}
