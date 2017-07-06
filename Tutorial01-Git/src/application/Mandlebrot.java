package application;

import org.apache.commons.math3.complex.Complex;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Mandlebrot extends TutorialDrawing implements Runnable, Redrawable {

	private Canvas _mainDrawingArea;
	private WritableImage _plotBuffer;
	private TutorialEventListener _evt;

	private static int MAX_ITERATIONS = 175;
	private static double MAX_IMAGINARY_SCALE = 4.0;
	private AnimationTimer _thread = new SimpleAnimationTimer (this);

	private boolean _threadActive = false;
	private boolean _stop = false;

	@Override
	public void run() {
		double minReal = -2.0;
		double maxReal = 1.0;

		double minImaginary = -1.0;
		double maxImaginary = 1.0;

		double xScaleFactor = (maxReal - minReal) / (_plotBuffer.getWidth() - 1);
		double yScaleFactor = (maxImaginary - minImaginary) / (_plotBuffer.getHeight() - 1);

		for (int xPos = 0; xPos < _plotBuffer.getWidth() && !_stop; ++xPos) {
			for (int yPos = 0; yPos < _plotBuffer.getHeight() && !_stop; ++yPos) {

				Complex z = new Complex(minReal + xPos * xScaleFactor, minImaginary + yPos * yScaleFactor);

				int iterations = 0;
				boolean draw = true;

				while (iterations < MAX_ITERATIONS && !_stop) {

					if ((z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary()) > MAX_IMAGINARY_SCALE) {
						draw = false;
						break;
					}

					z = z.multiply(z)
							.add(new Complex(minReal + xPos * xScaleFactor, minImaginary + yPos * yScaleFactor));

					iterations++;
				}

				if (draw) {
					_plotBuffer.getPixelWriter().setColor(xPos, yPos, Color.BLACK);
				} else
					_plotBuffer.getPixelWriter().setColor(xPos, yPos, Color.WHITE);
			}
		}

		_threadActive = false;
	}

	@Override
	public void execute(Canvas whereToDraw, TutorialEventListener evt) {
		_evt = evt;
		_mainDrawingArea = whereToDraw;
		_plotBuffer = new WritableImage((int) whereToDraw.getWidth(), (int) whereToDraw.getHeight());

		// Start data generation thread
		Thread genThread = new Thread(this);
		_threadActive = true;
		genThread.start();

		// Start animation thread
		_thread.start();
	}

	@Override
	public void redraw() {
		_mainDrawingArea.getGraphicsContext2D().drawImage(_plotBuffer, 0, 0);

		if (!_threadActive) {
			_thread.stop();
			_evt.OnDrawingStopped();
		}
	}

	@Override
	public void stopExecution() {
		_stop = true;
	}

}
