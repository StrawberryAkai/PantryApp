/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package cse110_project;

import java.io.*;
import java.util.ArrayList;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

class postRecipeCreate extends VBox {

    public String rName; 
    public String rDesc;
    public RecipeKind rKind;

    private Button saveRecipeButton; 
    private Button editRecipeButton;
    private Button backButton; 
    private TextArea recipeDescription;
    private Stage postCreateStage;
    private Scene scene;
    private boolean editflag;

    // display the generated recipe description in a new popout window
    // pmt = passed meal type, pml = passed meal ingredient list
    public postRecipeCreate(String pmt, String pml) {
        recipeGenerate rg = new recipeGenerate(pmt, pml);
        
        String ro = "";

        try {
            ro = rg.generate();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        rName = ro.substring(ro.indexOf(':')+2, ro.indexOf('('));
        rDesc = ro.substring(ro.indexOf("Ingredients"));
        rKind = RecipeKind.valueOf(ro.substring(ro.indexOf('(')+1, ro.indexOf(')')));

        postCreateStage = new Stage();
        recipeDescription = new TextArea();
        recipeDescription.setWrapText(true);
        recipeDescription.appendText(rDesc);
        editflag = false;
        recipeDescription.setEditable(editflag);


        saveRecipeButton = new Button("save");
        saveRecipeButton.setPrefSize(45, 15);
        saveRecipeButton.setAlignment(Pos.CENTER);

        editRecipeButton = new Button("edit");
        editRecipeButton.setPrefSize(45, 15);
        editRecipeButton.setAlignment(Pos.CENTER);

        backButton = new Button("back");
        backButton.setPrefSize(45, 15);
        backButton.setAlignment(Pos.CENTER);

        this.setPrefSize(500, 500);
    }

    public void postRecipeCreateDisplay() {
        saveRecipeButton.setOnAction(e -> {
            String updatedDesc = recipeDescription.getText();
            Recipe newRecipe = new Recipe(rName, rDesc, rKind);
            // ???
            //App.addRecipe(newRecipe);
            postCreateStage.close();
            
        });
        editRecipeButton.setOnAction(e -> {
            if(editflag == false){
                editflag = true;
                recipeDescription.setEditable(editflag);
            }
            else{
                editflag = false;
                recipeDescription.setEditable(editflag);
            }
        });
        backButton.setOnAction(e -> {
            postCreateStage.close();
        });

        HBox buttonArea = new HBox();
        buttonArea.getChildren().addAll(saveRecipeButton, editRecipeButton, backButton);
        buttonArea.setPadding(new Insets(10, 10, 10, 10));
        buttonArea.setSpacing(199);

        VBox recipeDetail = new VBox();
        recipeDetail.getChildren().addAll(recipeDescription);
        recipeDetail.setPadding(new Insets(10, 10, 10, 10));
        
        ScrollPane sp = new ScrollPane(recipeDetail);
        sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        this.getChildren().addAll(buttonArea, sp);

        scene = new Scene(this, 550, 550);

        postCreateStage.setTitle("Generated Recipe");
        postCreateStage.setResizable(false);
        postCreateStage.setScene(scene);
        postCreateStage.show();
    }
}

//This class creates prompts for user input
class Prompt extends HBox {
    private Label prompt;
    private Button micButton;
    private boolean isRecord;

    Prompt(){
        prompt = new Label();
        prompt.setPrefSize(500, 250);
        prompt.setWrapText(true);
        prompt.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        prompt.setAlignment(Pos.CENTER);

        micButton = new Button("Record");
        micButton.setMinHeight(250);

        this.getChildren().addAll(prompt, micButton);
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

    public void setLabel(String text){
        prompt.setText(text);
    }

    public boolean getRecordStatus(){
        return isRecord;
    }
}

class newScreen extends VBox {
    
    private static final String RESPONSE = "Your response is: ";
    private static final String MEAL_PROMPT = "Please select your meal type: Breakfast, Lunch, or Dinner";
    private static final String INGREDIENT_PROMPT = "Please list the ingredients you have";

    private Prompt mealPrompt;
    private Prompt ingredientPrompt;

    private Button mealTypeMicButton;
    private Button ingredientMicButton;
    private Button doneButton;

    private Label recordingLabel;

    private AudioRecord aRecord;

    private Stage inputStage;

    private Scene scene;

    private postRecipeCreate prc;

    private String mealType ; 
    private String mealList ; 

    String defaultLabelStyle = "-fx-font: 13 arial; -fx-pref-width: 175px; -fx-pref-height: 50px; -fx-text-fill: red; visibility: hidden";  

    newScreen() {
        inputStage = new Stage();

        recordingLabel = new Label("Recording...");
        recordingLabel.setAlignment(Pos.BOTTOM_CENTER);
        recordingLabel.setStyle(defaultLabelStyle);

        aRecord = new AudioRecord(recordingLabel);

        this.setAlignment(Pos.TOP_CENTER);
        this.setPrefSize(500, 800);

        doneButton = new Button("Done");

        mealPrompt = new Prompt();
        ingredientPrompt = new Prompt();

        mealTypeMicButton = mealPrompt.getMicButton();
        ingredientMicButton = ingredientPrompt.getMicButton();
    }

    public void voiceInputScreen() {
        mealPrompt.setLabel(MEAL_PROMPT);

        //Record and display user's response for meal type
        mealTypeMicButton.setOnAction(e -> {
            try {
                mealPrompt.toggleRecord();
                if(mealPrompt.getRecordStatus())
                    aRecord.startRecording();
                else{
                    aRecord.stopRecording();
                    mealType = getVoiceInput();
                    mealPrompt.setLabel(RESPONSE + mealType);
                    //Prompt user to input ingredient list after finish recording meal type
                    ingredientPrompt.setLabel(INGREDIENT_PROMPT);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        //Record and display user's response for ingredient list
        ingredientMicButton.setOnAction(e -> {
            try {
                ingredientPrompt.toggleRecord();
                if(ingredientPrompt.getRecordStatus())
                    aRecord.startRecording();
                else{
                    aRecord.stopRecording();
                    mealList = getVoiceInput();
                    ingredientPrompt.setLabel(RESPONSE + mealList);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        //Generate and display recipe page
        doneButton.setOnAction(e -> {
            try {
                prc = new postRecipeCreate(mealType, mealList);
                prc.postRecipeCreateDisplay();
                inputStage.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        this.getChildren().addAll(mealPrompt, ingredientPrompt, doneButton, recordingLabel);
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
}

class DetailedViewScreen extends VBox {

    private Button saveRecipeButton; 
    private Button editRecipeButton;
    private Button backButton; 
    private TextArea recipeDescription;
    private Stage postCreateStage;
    private Scene scene;
    private boolean saveflag;
    private Recipe tempR;

    // display the generated recipe description in a new popout window
    // pmt = passed meal type, pml = passed meal ingredient list
    DetailedViewScreen(Recipe r) {

        tempR = r;
        postCreateStage = new Stage();
        recipeDescription = new TextArea();
        recipeDescription.setWrapText(true);
        recipeDescription.appendText(tempR.getRecipeDescription());
        saveflag = false;
        recipeDescription.setEditable(saveflag);

        saveRecipeButton = new Button("save");
        saveRecipeButton.setPrefSize(45, 15);
        saveRecipeButton.setAlignment(Pos.CENTER);

        editRecipeButton = new Button("edit");
        editRecipeButton.setPrefSize(45, 15);
        editRecipeButton.setAlignment(Pos.CENTER);

        backButton = new Button("back");
        backButton.setPrefSize(45, 15);
        backButton.setAlignment(Pos.CENTER);

        this.setPrefSize(500, 500);
    }

    public void ShowDetailedView() {
        saveRecipeButton.setOnAction(e -> {
            String updatedDesc = recipeDescription.getText();
            tempR.setRecipeDescription(updatedDesc);
            postCreateStage.close();
        });
        editRecipeButton.setOnAction(e -> {
            if(saveflag == false){
                saveflag = true;
                recipeDescription.setEditable(saveflag);
            }
            else{
                saveflag = false;
                recipeDescription.setEditable(saveflag);
            }
        });
        backButton.setOnAction(e -> {
            postCreateStage.close();
        });

        HBox buttonArea = new HBox();
        buttonArea.getChildren().addAll(saveRecipeButton, editRecipeButton, backButton);
        buttonArea.setPadding(new Insets(10, 10, 10, 10));
        buttonArea.setSpacing(199);

        VBox recipeDetail = new VBox();
        recipeDetail.getChildren().addAll(recipeDescription);
        recipeDetail.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(buttonArea, recipeDetail);

        scene = new Scene(this, 550, 550);

        postCreateStage.setTitle("Generated Recipe");
        postCreateStage.setResizable(false);
        postCreateStage.setScene(scene);
        postCreateStage.show();
    }
}

// JavaFX Application main entry point
public class App extends Application {

    private newScreen ns;
    private DetailedViewScreen ds;

  public static void main(String[] args) {
        launch(args);
    }

    private RecipeStateManager state;
    public VBox recipesUI;

    private String rName;
    private String rDesc;
    private RecipeKind rKind;

    public void addRecipe(Recipe recipe) {
            state.addRecipe(recipe);
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
                Button title = new Button(recipe.getRecipeName());
                title.setAlignment(Pos.CENTER_LEFT);
                BorderPane.setAlignment(title, Pos.CENTER_LEFT);
                descInside.setLeft(title);
                title.setOnMouseClicked(e -> {
                    ds = new DetailedViewScreen(recipe);
                    ds.ShowDetailedView();
                    System.out.println(recipe.getRecipeDescription());
                });

                // recipe type
                Label recipeType = new Label(recipe.getRecipeKind().name());
                recipeType.setAlignment(Pos.CENTER_RIGHT);
                BorderPane.setAlignment(recipeType, Pos.CENTER_RIGHT);
                descInside.setRight(recipeType);

                recipeHbox.getChildren().add(descPane);
            }

            {
                StackPane delPane = new StackPane();
                Button deleteButton = new Button("Delete");
                delPane.getChildren().add(deleteButton);
                deleteButton.setOnMouseClicked(e -> {
                    state.deleteRecipe(recipe);
                    recipesUI.getChildren().remove(recipePane);
                });
                delPane.setPadding(new Insets(20.0));
                recipeHbox.getChildren().add(delPane);
            }

            recipePane.setPadding(new Insets(20.0));

            recipesUI.getChildren().add(recipePane);
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
            newRecipe.setMinHeight(50.0);

            newRecipe.setOnMouseClicked(e -> {
                ns.voiceInputScreen();
            });

            Region spacer = new Region();
            spacer.setMinWidth(50.0);
            titleHbox.getChildren().addAll(newRecipe, spacer);
            mainBox.getChildren().add(titleHbox);
        }

        recipesUI = new VBox();
        state = new RecipeStateManager();
        JSONOperations c = new JSONOperations(state);
        ArrayList<Recipe> tempRecipeList = c.readFromJSON(); //returns arraylist of recipes
        recipesUI.setAlignment(Pos.TOP_CENTER);
        for(Recipe r : tempRecipeList){
            addRecipe(r);
        }
        /* 
        for (int i = 0; i < 5; i++) {
            rName = "Recipe #" + i;
            rDesc = "";
            rKind = RecipeKind.valueOf("Dinner");
            Recipe toAdd = new Recipe(rName, rDesc, rKind);
            // for deleting recipes you probably want to store each recipe's UI object in the Recipe object and call delete through there
            
            addRecipe(toAdd);
        }
        */
        JSONOperations c2 = new JSONOperations(state);
        c2.writeToJSON();
        String testName = "beef soup";
        String testDesc = "beef, carrot, lettuce, water, salt";
        RecipeKind testKind = RecipeKind.valueOf("Dinner");
        Recipe testRecipe = new Recipe(testName, testDesc, testKind);
        addRecipe(testRecipe);
        c2.writeToJSON();
        // mainBox.getChildren().add(scrollPaneContents);
        ScrollPane pane = new ScrollPane();
        pane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            recipesUI.setPrefWidth(newValue.getWidth() - 1);
        });
        pane.setContent(recipesUI);
        mainBox.getChildren().add(pane);

        Scene scene = new Scene(mainBox, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
