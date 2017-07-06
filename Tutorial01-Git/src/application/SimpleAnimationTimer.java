package application;

import javafx.animation.AnimationTimer;

public class SimpleAnimationTimer extends AnimationTimer {
	private Redrawable _t;

	public SimpleAnimationTimer(Redrawable target) {
		_t = target;
	}

	@Override
	public void handle(long now) {
		_t.redraw();
	}

}
