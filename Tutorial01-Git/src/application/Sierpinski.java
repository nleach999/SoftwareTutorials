package application;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class Sierpinski extends TutorialDrawing implements Redrawable {

	private boolean _stop = false;

	private int _pauseFrames = 0;

	private static int FRAMES_TO_PAUSE = 60;
	
	// This code is not efficient enough to work past ~12 iterations.
	private static int MAX_ITERATIONS = 12;

	private int _iterations = 0;

	private TutorialEventListener _evt;

	private Canvas _draw;

	private Random _rnd = new Random(Calendar.getInstance().getTimeInMillis());
	
	private AnimationTimer _thread = new SimpleAnimationTimer (this);

	private class Point {
		private double _x, _y;

		public Point(double x, double y) {
			_x = x;
			_y = y;
		}

		public double getX() {
			return _x;
		}

		public double getY() {
			return _y;
		}

	}

	private class Triangle {
		private Point _top, _left, _right;

		private Color _clr;

		public Triangle(Point top, Point left, Point right, Color clr) {
			_clr = clr;
			_top = top;
			_left = left;
			_right = right;
		}

		public double[] getXArray() {
			return new double[] { _top.getX(), _left.getX(), _right.getX() };
		}

		public double[] getYArray() {
			return new double[] { _top.getY(), _left.getY(), _right.getY() };
		}

		Point getTop() {
			return _top;
		}

		Point getLeft() {
			return _left;
		}

		Point getRight() {
			return _right;
		}

		Color getColor() {
			return _clr;
		}
	}

	private ArrayList<Triangle> _polys = new ArrayList<Triangle>();

	@Override
	public void execute(Canvas whereToDraw, TutorialEventListener evtListener) {

		_evt = evtListener;
		_draw = whereToDraw;
		_draw.getGraphicsContext2D().setFill(getRandomColor () );

		Triangle initTriangle = new Triangle(new Point(_draw.getWidth() / 2.0, 0.0), new Point(0.0, _draw.getHeight()),
				new Point(_draw.getWidth(), _draw.getHeight()), getRandomColor ());
		_polys.add(initTriangle);

		
		// Start animation
		_thread.start();
	}

	@Override
	public void stopExecution() {
		_stop = true;
	}

	private Color getRandomColor() {
		return Color.color(_rnd.nextDouble(), _rnd.nextDouble(), _rnd.nextDouble());
	}

	@Override
	public void redraw() {

		if (_stop || _iterations == MAX_ITERATIONS) {
			_thread.stop();
			_evt.OnDrawingStopped();
		} else {

			if (_pauseFrames++ == 0) {
				_iterations++;

				ArrayList<Triangle> newPolys = new ArrayList<Triangle>();

				_draw.getGraphicsContext2D().clearRect(0, 0, _draw.getWidth(), _draw.getHeight());

				_draw.getGraphicsContext2D().strokeText(String.format("Triangles: %d Iterations: %d", 
						_polys.size(), _iterations), 0.0, 15.0);

				for (Triangle t : _polys) {
					_draw.getGraphicsContext2D().setFill(t.getColor());
					_draw.getGraphicsContext2D().fillPolygon(t.getXArray(), t.getYArray(), 3);

					Color newColor = getRandomColor();

					Point centerPoint = new Point(
							t.getLeft().getX() + ((t.getRight().getX() - t.getLeft().getX()) / 2.0),
							t.getTop().getY() + ((t.getLeft().getY() - t.getTop().getY()) / 2.0));

					newPolys.add(new Triangle(new Point(t.getTop().getX(), t.getTop().getY()),
							new Point(t.getLeft().getX() + ((centerPoint.getX() - t.getLeft().getX()) / 2.0),
									centerPoint.getY()),
							new Point(t.getRight().getX() - ((t.getRight().getX() - centerPoint.getX()) / 2.0),
									centerPoint.getY()),
							newColor));

					newPolys.add(new Triangle(
							new Point(t.getLeft().getX() + ((centerPoint.getX() - t.getLeft().getX()) / 2.0),
									centerPoint.getY()),
							new Point(t.getLeft().getX(), t.getLeft().getY()),
							new Point(centerPoint.getX(), t.getLeft().getY()), newColor));

					newPolys.add(new Triangle(
							new Point(t.getRight().getX() - ((t.getRight().getX() - centerPoint.getX()) / 2.0),
									centerPoint.getY()),
							new Point(centerPoint.getX(), t.getRight().getY()),
							new Point(t.getRight().getX(), t.getRight().getY()), newColor));

				}

				// Replace all the previous triangles with the new ones.
				_polys = newPolys;
			} else if (_pauseFrames++ == FRAMES_TO_PAUSE)
				_pauseFrames = 0;

		}

	}

}
