package application;

import javafx.scene.canvas.Canvas;

public abstract class TutorialDrawing {
	
	public abstract void execute(Canvas whereToDraw, TutorialEventListener evtListener);
	public abstract void stopExecution ();

}
