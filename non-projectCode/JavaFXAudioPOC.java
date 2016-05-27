import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class JavaFXAudioPOC extends Application{

    private MediaPlayer mediaPlayer;

    public static void main(String args[]){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        File fileForMusic = new File("C:\\Users\\Eric\\Music\\New stuff\\Prism.mp3");

        if (!fileForMusic.exists())
        {
            System.out.println("File does not exist");
            return;
        }
        System.out.println(fileForMusic.toURI().toString());
        
		// Create the m
		Media song = new Media(fileForMusic.toURI().toString());
        final MediaPlayer player = new MediaPlayer(song);
        player.setAutoPlay(true);
        player.setOnError(new Runnable() {
            @Override
            public void run() {
                System.out.println("ERROR");
                MediaException e = player.getError();
                e.printStackTrace();

            }
        });
        System.out.println(player.getStatus());
        System.out.println(player);
        System.out.println(player.getMedia());

        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                System.out.println("Player running?");
                player.play();
            }
        });
        // Add a mediaView, to display the media. Its necessary !
        // This mediaView is added to a Pane
        MediaView mediaView = new MediaView(player);
        mediaView.setMediaPlayer(player);

        // Setup the Java FX Scene
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        root.getChildren().add(mediaView);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();

    }

}
