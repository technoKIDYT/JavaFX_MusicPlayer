package com.music.Contoller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MusicController implements Initializable {

	@FXML
	private ImageView trackLogo, prev, playpause, next;

	@FXML
	private JFXSlider time;

	@FXML
	private JFXHamburger ham;

	@FXML
	private AnchorPane leftSidePanel;

	@FXML
	private JFXDrawer drawer;

	@FXML
	private Label songTitle, playTime;

	@FXML
	private MediaView mediaView;

	private MediaPlayer mediaplayer;
	private final boolean repeat = false;
	private boolean stopRequested = false;
	private boolean atEndOfMedia = false;
	private Duration duration;
	private Media media;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			// Your Path Here
			File f = new File("C:\\\\Users\\\\sushi\\\\Downloads\\\\Music\\\\Love me like you do.mp3");
			media = new Media(f.toURI().toString());
			mediaplayer = new MediaPlayer(media);
			// mediaplayer.setAutoPlay(true);
			mediaView.setMediaPlayer(mediaplayer);
			time.setValue(1);
			String title = f.getAbsoluteFile().getName();
			System.out.println(title);
			songTitle.setText(title);
		} catch (Exception e) {
			e.printStackTrace();
		}

		drawer.setSidePane(leftSidePanel);
		HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(ham);
		transition.setRate(-1);
		drawer.open();
		ham.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			transition.setRate(transition.getRate() * -1);
			transition.play();

			if (drawer.isOpened()) {
				drawer.close();
			} else {
				drawer.open();
			}
		});

		playpause.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				System.out.println("I am here");
				Status status = mediaplayer.getStatus();
				System.out.println(status);
				if (status == Status.UNKNOWN || status == Status.HALTED) {
					// don't do anything in these states
					return;
				}

				if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
					// rewind the movie if we're sitting at the end
					if (atEndOfMedia) {
						mediaplayer.seek(mediaplayer.getStartTime());
						atEndOfMedia = false;
					}
					mediaplayer.play();
				} else {
					mediaplayer.pause();
				}
			}
		});

		mediaplayer.currentTimeProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateValues();
			}
		});

		mediaplayer.setOnPlaying(new Runnable() {
			public void run() {
				if (stopRequested) {
					mediaplayer.pause();
					stopRequested = false;
				} else {
					// Will change the image
					playpause.setImage(new Image(
							"file:\\" + System.getProperty("user.dir") + "\\externalImages\\pause_100px.png"));
				}
			}
		});

		mediaplayer.setOnPaused(new Runnable() {
			public void run() {
				System.out.println("onPaused");
				// here too
				playpause.setImage(
						new Image("file:\\" + System.getProperty("user.dir") + "\\externalImages\\play_100px.png"));
			}
		});

		mediaplayer.setOnReady(new Runnable() {
			public void run() {
				duration = mediaplayer.getMedia().getDuration();
				updateValues();
			}
		});

		mediaplayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
		mediaplayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				if (!repeat) {
					// will update image here too
					// playButton.setText(">");
					stopRequested = true;
					atEndOfMedia = true;
				}
			}
		});

		time.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (time.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					System.out.println(time.getValue());
					mediaplayer.seek(duration.multiply(time.getValue() / 100.0));
					time.setValue(time.getValue());
				}
			}
		});
	}

	protected void updateValues() {
		if (playTime != null && time != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mediaplayer.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					time.setDisable(duration.isUnknown());
					if (!time.isDisabled() && duration.greaterThan(Duration.ZERO) && !time.isValueChanging()) {
						time.setValue(currentTime.divide(duration).toMillis() * 100.0);
					}
					/*
					 * if (!volumeSlider.isValueChanging()) { volumeSlider.setValue((int)
					 * Math.round(mp.getVolume() * 100)); }
					 */
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
			}
		}
	}
}
