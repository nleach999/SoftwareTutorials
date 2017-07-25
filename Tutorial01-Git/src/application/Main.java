package application;

import org.apache.commons.math3.complex.Complex;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

// I am making a change.
// Another comment.
public class Main extends Application implements TutorialEventListener {

	@Override
	public void start(Stage primaryStage) {
		try {

			primaryStage.setResizable(false);

			GridPane root = new GridPane();

			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			setupUI(root);

			primaryStage.setTitle("Tutorial");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Canvas _mainDrawingArea = new Canvas();
	
	private ObservableList<String> _drawOptions = FXCollections.observableArrayList("Mandlebrot", 
			"Sierpinski Gasket", "Walker");
	
	private ComboBox<String> _drawingSelection = new ComboBox<String>(_drawOptions);
	private Button _stopButton, _startButton, _clearButton;

	private TutorialDrawing _runningDrawing;

	private static double CANVAS_HEIGHT_RATIO = .90;

	private void setupUI(GridPane root) {
		_mainDrawingArea.setHeight(root.getHeight() * CANVAS_HEIGHT_RATIO);
		_mainDrawingArea.setWidth(root.getWidth());

		GridPane.setRowIndex(_mainDrawingArea, 0);
		GridPane.setColumnSpan(_mainDrawingArea, 3);

		_startButton = new Button();
		_startButton.setText("Start");
		_startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				OnStartButton();
			}
		});
		GridPane.setRowIndex(_startButton, 1);
		GridPane.setColumnIndex(_startButton, 0);

		_stopButton = new Button();
		_stopButton.setText("Stop");
		_stopButton.setDisable(true);
		_stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				OnStopButton();
			}
		});
		GridPane.setRowIndex(_stopButton, 1);
		GridPane.setColumnIndex(_stopButton, 1);

		_clearButton = new Button();
		_clearButton.setText("Clear");
		_clearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				OnClearButton();
			}
		});
		GridPane.setRowIndex(_clearButton, 1);
		GridPane.setColumnIndex(_clearButton, 2);

		GridPane.setRowIndex(_drawingSelection, 1);
		GridPane.setColumnIndex(_drawingSelection, 3);

		root.getChildren().addAll(_mainDrawingArea, _startButton, _clearButton, _drawingSelection, _stopButton);
	}

	private void OnStartButton() {
		String selectedDrawing = (String) _drawingSelection.getValue();

		if (selectedDrawing != null) {

			_runningDrawing = null;

			if (selectedDrawing.compareTo("Mandlebrot") == 0)
				_runningDrawing = new Mandlebrot();

			 if (selectedDrawing.compareTo("Sierpinski Gasket") == 0)
					_runningDrawing = new Sierpinski();

			 if (selectedDrawing.compareTo("Walker") == 0)
					_runningDrawing = new Walker();

			if (_runningDrawing != null) {
				_stopButton.setDisable(false);
				_startButton.setDisable(true);
				_clearButton.setDisable(true);
				_runningDrawing.execute(_mainDrawingArea, this);
			}
		}
	}

	private void OnClearButton() {
		_mainDrawingArea.getGraphicsContext2D().clearRect(0, 0, _mainDrawingArea.getWidth(),
				_mainDrawingArea.getHeight());
	}

	private void OnStopButton() {
		_runningDrawing.stopExecution();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void OnDrawingStopped() {
		_runningDrawing = null;
		_startButton.setDisable(false);
		_stopButton.setDisable(true);
		_clearButton.setDisable(false);
	}
}
