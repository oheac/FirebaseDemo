package aydin.firebasedemo;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import okhttp3.*;

import java.io.IOException;

public class LoginController {

    // REDACTED
    private static String API_KEY = "REDACTED";
    private static String AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button loginButton;

    @FXML
    public Button registerButton;

    void initialize() {

    }

    @FXML
    public boolean registerUser() {
        String email = usernameField.getText();
        String username = usernameField.getText().split("@")[0];
        String password = passwordField.getText();

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setDisplayName(username);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }

    }

    @FXML
    public void loginUser() throws IOException {
        String email = usernameField.getText();
        String username = usernameField.getText().split("@")[0];
        String password = passwordField.getText();

        try {
            String idToken = login(email, password);
            System.out.println("Logged in! ID Token: " + idToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DemoApp.setRoot("primary");
    }

    public static String login(String email, String password) throws IOException {
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory requestFactory = transport.createRequestFactory();

        // Prepare the JSON body
        JsonObject payload = new JsonObject();
        payload.addProperty("email", email);
        payload.addProperty("password", password);
        payload.addProperty("returnSecureToken", true);

        // Build the request
        GenericUrl url = new GenericUrl(AUTH_URL);
        HttpContent content = new ByteArrayContent("application/json", payload.toString().getBytes());

        HttpRequest request = requestFactory.buildPostRequest(url, content);
        request.setParser(new com.google.api.client.json.JsonObjectParser(new GsonFactory()));

        // Execute and handle response
        HttpResponse response = request.execute();
        try {
            String responseString = response.parseAsString();
            JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();
            return jsonResponse.get("idToken").getAsString();
        } finally {
            response.disconnect();
        }
    }

    @FXML
    public void loginButtonClicked(ActionEvent actionEvent) throws IOException {
        loginUser();
    }

    @FXML
    public void registerButtonClicked(ActionEvent actionEvent) {
        registerUser();
    }
}
