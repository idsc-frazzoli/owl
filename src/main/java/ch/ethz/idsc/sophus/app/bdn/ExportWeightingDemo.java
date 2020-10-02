// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.math.ClipCover;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Rescale;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ abstract class ExportWeightingDemo extends ScatteredSetWeightingDemo implements ActionListener {
  private static final int REFINEMENT = 120; // presentation 60
  private final JButton jButtonExport = new JButton("export");

  public ExportWeightingDemo( //
      boolean addRemoveControlPoints, List<GeodesicDisplay> list, List<LogWeighting> array) {
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
        geodesicDisplay().toString(), //
        logWeighting.toString());
    root.mkdirs();
    for (Biinvariant biinvariant : distinct()) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
          biinvariant, //
          geodesicDisplay().vectorLogManifold(), //
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
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay().geodesicArrayPlot();
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence.length());
    Tensor wgs = geodesicArrayPlot.raster(refinement, tensorUnaryOperator, fallback);
    List<Integer> dims = Dimensions.of(wgs);
    Tensor wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    Rescale rescale = new Rescale(wgp);
    Clip clip = ClipCover.of(rescale.scalarSummaryStatistics());
    return new ArrayPlotRender( //
        rescale.result(), //
        logWeighting().equals(LogWeightings.DISTANCES) ? ClipCover.of(clip, RealScalar.ZERO) : clip, //
        colorDataGradient(), magnification);
  }

  private static List<Biinvariant> distinct() {
    return Arrays.asList( //
        Biinvariants.METRIC, //
        Biinvariants.TARGET, //
        Biinvariants.GARDEN, //
        Biinvariants.HARBOR);
  }
}