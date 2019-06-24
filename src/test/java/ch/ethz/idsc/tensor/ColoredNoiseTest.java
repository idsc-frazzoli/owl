// code by jph
package ch.ethz.idsc.tensor;

import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ColoredNoiseTest extends TestCase {
  public void testSimple() {
    ColoredNoise coloredNoise = new ColoredNoise(1.2);
    DoubleSummaryStatistics doubleSummaryStatistics = //
        DoubleStream.generate(coloredNoise::nextValue) //
            .limit(1000).summaryStatistics();
    double average = doubleSummaryStatistics.getAverage();
    double min = doubleSummaryStatistics.getMin();
    double max = doubleSummaryStatistics.getMax();
    Clips.absoluteOne().requireInside(RealScalar.of(average));
    Clips.interval(-20, 0).requireInside(RealScalar.of(min));
    Clips.interval(0, +20).requireInside(RealScalar.of(max));
  }
}
