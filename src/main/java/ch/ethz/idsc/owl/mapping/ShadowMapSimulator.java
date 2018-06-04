package ch.ethz.idsc.owl.mapping;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;

public class ShadowMapSimulator extends ShadowMap {
  private boolean isPaused = false;
  private Timer increaserTimer;

  public ShadowMapSimulator(LidarEmulator lidar, ImageRegion imageRegion, Supplier<StateTime> stateTimeSupplier, float vMax, float rMin) {
    super(lidar, imageRegion, stateTimeSupplier, vMax, rMin);
  }

  public final void startNonBlocking(int updateRate) {
    TimerTask mapUpdate = new TimerTask() {
      @Override
      public void run() {
        if (!isPaused)
          updateMap(stateTimeSupplier.get(), 1.0f / updateRate);
      }
    };
    increaserTimer = new Timer("MapUpdateTimer");
    increaserTimer.scheduleAtFixedRate(mapUpdate, 10, 1000 / updateRate);
  }

  public final void flagShutdown() {
    increaserTimer.cancel();
  }

  public final void pause() {
    isPaused = true;
  }

  public final void resume() {
    isPaused = false;
  }
}
