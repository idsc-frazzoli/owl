// code by jph
package ch.ethz.idsc.owl.plot;

import java.awt.Dimension;
import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

enum ListPlotDemo {
  ;
  public static void main(String[] args) throws IOException {
    SeriesCollection seriesCollection = new SeriesCollection();
    SeriesContainer sc0 = seriesCollection.add(Tensors.fromString("{{1,-0.3},{2,2},{2.3,1},{3.1,1.5},{12,3}}"));
    sc0.setJoined(true);
    sc0.setName("joined");
    SeriesContainer sc1 = seriesCollection.add(Tensors.fromString("{{0.2,0},{1.5,2},{1.9,1},{3,2.9},{4.5,1.9}}"));
    sc1.setName("scatter");
    Distribution d = NormalDistribution.standard();
    Tensor range = Subdivide.of(3, 15, 100);
    SeriesContainer sc2 = seriesCollection.add(range, RandomVariate.of(d, range.length()));
    sc2.setName("normal");
    ListPlot.of( //
        seriesCollection, //
        new Dimension(400, 300), //
        UserHome.Pictures("listplot.png"));
  }
}
