import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App extends Application {
    //vairables
    private Button connectButton = new Button("Connect to Database");
    private Button testButton = new Button("Run Tests");
    private Label statusLabel = new Label();
    private Connection connection;
    //basic app setup using javafx
    @Override
    public void start(Stage primaryStage){
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(connectButton, testButton);
        hBox.setAlignment(Pos.CENTER);

        gridPane.add(hBox, 0, 0);
        gridPane.add(statusLabel, 0, 1);

        connectButton.setOnAction(e -> connect(primaryStage));
        testButton.setOnAction(e -> runTests());

        Scene scene = new Scene(gridPane, 400, 200);
        primaryStage.setTitle("sql batch vs not");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //connects to the sql database
    private void connect(Stage primaryStage){
        connection = DBConnectionPanel.showDialog(primaryStage);
        if(connection != null){
            statusLabel.setText("Database is connected yippie");
        }
    }
    //checks if your connected - if so runs the tests and displays the results
    private void runTests(){
        if(connection == null){
            statusLabel.setText("Bro you didnt connect - please do that?");
            return;
        }
        long timeWithoutBatch = insertRecords(false);
        long timeWithBatch = insertRecords(true);
        statusLabel.setText(String.format("Without Batch: %d ms\nWith Batch: %d ms", timeWithoutBatch, timeWithBatch));
    }
    //inserts into the database
    private long insertRecords(boolean useBatch){
        String query = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            long startTime = System.currentTimeMillis();
            for(int i = 0; i < 1000; i++){
                preparedStatement.setDouble(1, Math.random());
                preparedStatement.setDouble(2, Math.random());
                preparedStatement.setDouble(3, Math.random());
                if(useBatch){
                    preparedStatement.addBatch();
                }else{
                    preparedStatement.executeUpdate();
                }

                if(useBatch && i % 100 == 0){
                    preparedStatement.executeBatch();
                }
            }

            if(useBatch){
                preparedStatement.executeBatch();
            }
            long endTime = System.currentTimeMillis();
            return endTime - startTime;
        }catch(SQLException ex){
            statusLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
            return -1;
        }
    }
// runs the app
    public static void main(String[] args){
        launch(args);
    }
}