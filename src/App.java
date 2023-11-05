import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

class Footer extends HBox{
    private Button micButton;
    private Button doneButton;
    private Button nextButton;
    private boolean isRecord;

    Footer(){
        this.setPrefSize(500, 60);
        this.setSpacing(15);

        doneButton = new Button("Done");
        doneButton.setMinHeight(25.0);

        micButton = new Button("Record");
        micButton.setMinHeight(25.0);
        isRecord = false;

        nextButton = new Button("Next");
        nextButton.setMinHeight(25.0);

        this.getChildren().addAll(micButton, nextButton, doneButton);
        this.setAlignment(Pos.CENTER);
    }

    public void toggleRecord(){
        if(!isRecord){
            isRecord = true;
        }
        else{
            isRecord = false;
        }
    }

    public Button getMicButton() {
        return this.micButton;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }

    public Button getNextButton() {
        return this.nextButton;
    }

    public boolean getRecordStatus(){
        return isRecord;
    }
}

class newScreen extends VBox {
    
    private static final String RESPONSE = "Your response is: ";
    private static final String MEAL_PROMPT = "Please select your meal type: Breakfast, Lunch, or Dinner";
    private static final String INGREDIENT_PROMPT = "Please list the ingredients you have";

    private Footer footer;
    private Button micButton;
    private Button doneButton;
    private Button nextButton;

    private Label response;

    private Label recordingLabel;

    private AudioRecord aRecord;

    private Stage inputStage;

    private Scene scene;

    String defaultLabelStyle = "-fx-font: 13 arial; -fx-pref-width: 175px; -fx-pref-height: 50px; -fx-text-fill: red; visibility: hidden";  

    newScreen() {
        inputStage = new Stage();

        recordingLabel = new Label("Recording...");
        recordingLabel.setAlignment(Pos.BOTTOM_CENTER);
        recordingLabel.setStyle(defaultLabelStyle);

        aRecord = new AudioRecord(recordingLabel);

        this.setAlignment(Pos.TOP_CENTER);
        this.setPrefSize(500, 800);

        footer = new Footer();
        micButton = footer.getMicButton();
        nextButton = footer.getNextButton();
        doneButton = footer.getDoneButton();

        response = new Label();
        response.setPrefSize(500, 500);
        response.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        response.setAlignment(Pos.CENTER); 
    }

    public void voiceInputScreen() {
        response.setText(MEAL_PROMPT);

        micButton.setOnAction(e -> {
            try {
                footer.toggleRecord();
                if(footer.getRecordStatus())
                    aRecord.startRecording();
                else{
                    aRecord.stopRecording();
                    String voiceString = RESPONSE;
                    voiceString += getVoiceInput();
                    response.setText(voiceString);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        nextButton.setOnAction(e -> {
            response.setText(INGREDIENT_PROMPT);
        });

        doneButton.setOnAction(e -> {
            try {
                
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        this.getChildren().addAll(response, footer, recordingLabel);
        this.setSpacing(15);


        scene = new Scene(this, 600, 600);

        inputStage.setTitle("Create Recipe");
        inputStage.setResizable(false);
        inputStage.setScene(scene);
        inputStage.show();
    }

    private String getVoiceInput()throws Exception{
        Whisper voiceInput = new Whisper();

        // Create file object from file path
        String path = "recording.wav";
        File file = new File(path);
        
        String result = voiceInput.handleVoiceInput(file);

        return result;
    }

    /*
     * For Creat Recipe
     */

    // private String processVoiceInput()throws Exception{
    //     ChatGPT recipe = new ChatGPT();
    //     String result = recipe.processInput();
    //     return result;
    // }
}

// JavaFX Application main entry point
public class App extends Application {

    private newScreen ns;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Recipe Run");

        ns = new newScreen();

        VBox mainBox = new VBox();
        mainBox.setAlignment(Pos.TOP_CENTER);

        // top titlebar
        {
            HBox titleHbox = new HBox();
            titleHbox.setAlignment(Pos.CENTER_RIGHT);
            Button newRecipe = new Button("New Recipe");
            newRecipe.setOnMouseClicked(e -> {
                ns.voiceInputScreen();
            });
            newRecipe.setMinHeight(50.0);
            Region spacer = new Region();
            spacer.setMinWidth(50.0);
            titleHbox.getChildren().addAll(newRecipe, spacer);
            mainBox.getChildren().add(titleHbox);
        }

        VBox scrollPaneContents = new VBox();
        scrollPaneContents.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < 20; i++) {
            StackPane recipePane = new StackPane();

            HBox.setHgrow(recipePane, Priority.ALWAYS);

            HBox recipeHbox = new HBox(20.0);
            recipePane.getChildren().add(recipeHbox);
            StackPane.setAlignment(recipeHbox, Pos.CENTER);
            recipeHbox.setStyle("-fx-border-color: black; -fx-border-width: 1;");

            recipeHbox.setMinHeight(100.0);

            {
                StackPane descPane = new StackPane();
                HBox.setHgrow(descPane, Priority.ALWAYS);

                BorderPane descInside = new BorderPane();
                descInside.setPadding(new Insets(20.0));
                descInside.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                descPane.getChildren().add(descInside);

                // recipe title
                Label title = new Label("Recipe");
                title.setAlignment(Pos.CENTER_LEFT);
                BorderPane.setAlignment(title, Pos.CENTER_LEFT);
                descInside.setLeft(title);

                // recipe type
                Label recipeType = new Label("Breakfast");
                recipeType.setAlignment(Pos.CENTER_RIGHT);
                BorderPane.setAlignment(recipeType, Pos.CENTER_RIGHT);
                descInside.setRight(recipeType);

                recipeHbox.getChildren().add(descPane);
            }

            {
                StackPane delPane = new StackPane();
                delPane.getChildren().add(new Button("Delete"));
                delPane.setPadding(new Insets(20.0));
                recipeHbox.getChildren().add(delPane);
            }

            recipePane.setPadding(new Insets(20.0));

            scrollPaneContents.getChildren().add(recipePane);
        }
        // mainBox.getChildren().add(scrollPaneContents);
        ScrollPane pane = new ScrollPane();
        pane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            scrollPaneContents.setPrefWidth(newValue.getWidth() - 1);
        });
        pane.setContent(scrollPaneContents);
        mainBox.getChildren().add(pane);

        Scene scene = new Scene(mainBox, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}