import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by kwasi on 4/21/2017.
 */
public class Client extends Application {

    int portNo = 5202;
    DataInputStream dataIn = null;
    DataOutputStream dataOut = null;
    //String serverMessage = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea inputArea = new TextArea();
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setFont(Font.font(30));
        outputArea.setPromptText("Start Here");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(outputArea);
        borderPane.setBottom(inputArea);

        Scene scene = new Scene(borderPane, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Client");
        primaryStage.show();



        new Thread(()->{
            try {
                Socket socket = new Socket("LocalHost",portNo);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());

                new Thread(() ->{
                    try{
                        //Socket socket = new Socket("localHost",portNo);
                        //dataIn = new DataInputStream(socket.getInputStream());
                        //dataOut = new DataOutputStream(socket.getOutputStream());
                        while (true){
                            String serverMessage = dataIn.readUTF();

                            Platform.runLater(() -> {
                                outputArea.appendText(serverMessage + '\n');
                            });
                        }

                    }
                    catch (IOException e){
                        System.err.println(e);
                    }
            }).start();

                inputArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.ENTER){
                            sendMessage();
                        }
                    }
                    public void sendMessage(){
                        try{
                            String message = "Client: " + inputArea.getText().toString().trim();
                            dataOut.writeUTF(message);
                            dataOut.flush();

                            Platform.runLater(() -> {
                                outputArea.appendText(message);
                                inputArea.setText("");
                            });

                        }
                        catch (Exception e){
                            System.err.println(e);
                        }
                    }
                });

            }
            catch (Exception e){
                outputArea.appendText(e.toString() + '\n');
            }


        }).start();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
