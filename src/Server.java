import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Server extends Application {
    //Port number used
    int portNo = 5202;


    @Override
    public void start(Stage primaryStage) throws Exception {

        //Gui Layout
        TextArea inputArea = new TextArea();
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(outputArea);
        borderPane.setBottom(inputArea);

        Scene scene = new Scene(borderPane, 600,500);
        // Starting Application
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

        //new Thread to handle network
        new Thread(() -> {
            try{
                //Server socket creation
                ServerSocket serverSocket = new ServerSocket(portNo);
                //Print server start time
                Platform.runLater(() -> {
                    outputArea.appendText("Server started at " + new Date() + '\n');
                    outputArea.appendText("current port number: " + portNo + "\n" );
                });

                Socket socket = serverSocket.accept();

                // I/O stream creation
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

                String message = "";

                // Server communications
                while (!message.equals("quit")){
                    //Saving client input
                    String clientMessage = dataIn.readUTF();
                    // Sending a message
                    dataOut.writeUTF(message);
                    dataOut.flush();

                    // print message to screen
                    Platform.runLater(() -> {
                        outputArea.appendText( clientMessage +  '\n' );
                        //outputArea.appendText(message + '\n');
                    });

                    // managing ENTER key execution
                    inputArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            if (event.getCode() == KeyCode.ENTER){
                                sendMessage();
                            }
                        }
                        //  send message function
                        public void sendMessage(){
                            try{
                                String message = "Server: " + inputArea.getText().toString().trim();
                                dataOut.writeUTF(message);
                                dataOut.flush();

                                Platform.runLater(() -> {
                                    outputArea.appendText(message + '\n');
                                    inputArea.setText("");
                                });

                            }
                            catch (Exception e){
                                System.err.println(e);
                            }
                        }
                    });
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        //  start thread
        }).start();

    }


    // Main function
    public static void main(String[] args) {
        launch(args);
    }
}
