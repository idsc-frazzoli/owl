package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** PID control */
public class PIDControl extends StateTrajectoryControl {
	private final Clip clip;
	private final Scalar lookAhead;

	public PIDControl(Scalar lookAhead, Scalar maxTurningRate) {
		this.lookAhead = lookAhead;
		this.clip = Clips.interval(maxTurningRate.negate(), maxTurningRate);
		// TODO Auto-generated constructor stub
	}

	@Override // from StateTrajectoryControl
	protected Scalar pseudoDistance(Tensor x, Tensor y) {
		return Norm2Squared.ofVector(Se2Wrap.INSTANCE.difference(x, y));
	}

	@Override // from AbstractEntity
	protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
		// TODO Auto-generated method stub
		return null;
	}

}
