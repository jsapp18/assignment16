import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionPanel extends GridPane {
    //sets up the textfields with prefilled info
    private TextField tfUrl = new TextField("jdbc:mysql://localhost:3306/assignment16");
    private TextField tfUsername = new TextField("root");
    private PasswordField pfPassword = new PasswordField();
    private Button btConnect = new Button("Connect to Database");
    private Label lblStatus = new Label();
    private Connection connection;
    //constructor that makes the panel
    public DBConnectionPanel() {
        setPadding(new Insets(10));
        setHgap(5);
        setVgap(5);
        setAlignment(Pos.CENTER);
        add(new Label("Database URL:"), 0, 0);
        add(tfUrl, 1, 0);
        add(new Label("Username:"), 0, 1);
        add(tfUsername, 1, 1);
        add(new Label("Password:"), 0, 2);
        add(pfPassword, 1, 2);
        add(btConnect, 1, 3);
        add(lblStatus, 1, 4);
        btConnect.setOnAction(e -> connect());
    }
    //connects to database
    private void connect() {
        try {
            connection = DriverManager.getConnection(tfUrl.getText(), tfUsername.getText(), pfPassword.getText());
            lblStatus.setText("Connected to the database");
        } catch (SQLException ex) {
            lblStatus.setText("Connection failed: " + ex.getMessage());
        }
    }
    //read it
    public Connection getConnection() {
        return connection;
    }
    //makes the scene
    public static Connection showDialog(Stage primaryStage) {
        DBConnectionPanel pane = new DBConnectionPanel();
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        Scene scene = new Scene(pane, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
        return pane.getConnection();
    }
}