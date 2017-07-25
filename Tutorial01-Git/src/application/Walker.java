package application;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.util.Pair;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

// Adding comment to simulate changes by multiple users.
public class Walker extends TutorialDrawing implements Redrawable {

	private SimpleAnimationTimer _thread = new SimpleAnimationTimer(this);

	private int _delay = 0;
	private static int MAX_DRAW_DELAY = 3;
	
	private Canvas _draw;

	private TutorialEventListener _evt;

	private static int CELLS = 9;
	private static int ROWS = 8;

	Random _rnd = new Random(Calendar.getInstance().getTimeInMillis());

	private Image[][] _cells = new Image[ROWS][CELLS];

	// Set directional scalars. S SW W NW N NE E SE
	private static double[] _xDir = new double[] { 0.0, -1.0, -1.0, -1.0, 0.0, 1.0, 1.0, 1.0 };
	private static double[] _yDir = new double[] { 1.0, 1.0, 0.0, -1.0, -1.0, -1.0, 0.0, 1.0 };

	private boolean _stop = false;

	private Image[] loadCells(Image src, int row) {
		Image[] cells = new Image[CELLS];

		for (int x = 0; x < CELLS; x++) {
			cells[x] = loadCell(src, row, x);
		}

		return cells;
	}

	private Image loadCell(Image src, int row, int column) {
		WritableImage img = new WritableImage(src.getPixelReader(), _cellWidth * column, _cellHeight * row, _cellWidth,
				_cellHeight);

		return img;
	}

	private void onMouseClick(MouseEvent e) {
		
		// Switch direction if the mouse is clicked on the avatar
		if ((e.getX() >= _curX && e.getX() <= _curX + _cellWidth)
				|| (e.getY() >= _curY && e.getY() <= _curY + _cellHeight))
			_moving = false;

	}

	@Override
	public void execute(Canvas whereToDraw, TutorialEventListener evtListener) {
		_draw = whereToDraw;
		_evt = evtListener;
		
		_draw.getGraphicsContext2D().setFill(Color.WHITE);
		_draw.getGraphicsContext2D().fillRect(0,  0,  _draw.getWidth(), _draw.getHeight());

		_draw.setOnMouseClicked((evt) -> onMouseClick(evt));

		InputStream walkerCells = getClass().getResourceAsStream("/resources/walker.jpg");

		Image img = new Image(walkerCells);

		_cellWidth = (int) (img.getWidth() / CELLS);
		_cellHeight = (int) (img.getHeight() / ROWS);

		for (int row = 0; row < ROWS; row++) {
			_cells[row] = loadCells(img, row);
		}

		_thread.start();
	}

	@Override
	public void stopExecution() {
		_stop = true;
	}

	private int _cellWidth;
	private int _cellHeight;

	private boolean _moving = false;
	private double _curX = 0.0;
	private double _curY = 0.0;

	private void setDirection() {
		if (!_moving) {

			_moving = true;

			_nextRow = _rnd.nextInt(CELLS - 1);

			_facingDest = false;
		}

	}

	private int _curRow = 0;
	private int _nextRow = 0;
	private boolean _facingDest = false;
	private boolean _turning = false;

	private void faceDestination() {
		if (!_facingDest) {

			if (!_turning) {
				_turning = true;
				_curCell = 0;
			}

			if (_turning) {
				if (_curRow < _nextRow)
					_curRow++;
				else if (_curRow > _nextRow)
					_curRow--;
				else {
					_turning = false;
					_facingDest = true;
				}
			}

		}
	}

	private int _curCell = 0;
	private void executeNextStep() {

		if (_facingDest) {
			double nextX = _curX + _xDir[_curRow];
			double nextY = _curY + _yDir[_curRow];
			if (++_curCell == CELLS)
				_curCell = 0;

			if (nextX >= _draw.getWidth() - _cellWidth || nextX < 0.0 || nextY >= _draw.getHeight() - _cellHeight
					|| nextY < 0.0)
				_moving = false;
			else {
				_curX = nextX;
				_curY = nextY;
			}

		}

	}

	@Override
	public void redraw() {

		if (_stop) {
			_thread.stop();
			_evt.OnDrawingStopped();
		} else if (++_delay == MAX_DRAW_DELAY) {
			_delay = 0;

			setDirection();
			faceDestination();
			executeNextStep();

			_draw.getGraphicsContext2D().drawImage(_cells[_curRow][_curCell], _curX, _curY);

		}

	}

}
