/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package cse110_project;

import java.io.*;
import java.util.ArrayList;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    private Button saveRecipeButton; 
    private Button editRecipeButton;
    private Button backButton; 
    private Label recipeDescription;
    private Stage postCreateStage;
    private Scene scene;

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

        rName = ro.substring(ro.indexOf(':')+2, ro.indexOf("Ingredients"));
        rDesc = ro.substring(ro.indexOf("Ingredients"));

        postCreateStage = new Stage();
        
        recipeDescription = new Label(ro);
        recipeDescription.setAlignment(Pos.CENTER);
        recipeDescription.setWrapText(true);

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
            //todo
            System.out.println(rName);
            System.out.println(rDesc);
        });
        editRecipeButton.setOnAction(e -> {
            //todo
        });
        backButton.setOnAction(e -> {
            //todo
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
    private Label infoLabel;

    private AudioRecord aRecord;

    private Stage inputStage;

    private Scene scene;

    private String information;

    private postRecipeCreate prc;

    private String mealType ; 
    private String mealList ; 

    private boolean recordingIntoList = false; 

    String defaultLabelStyle = "-fx-font: 13 arial; -fx-pref-width: 175px; -fx-pref-height: 50px; -fx-text-fill: red; visibility: hidden";  

    newScreen() {
        inputStage = new Stage();

        information = "Meal type: ";

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

        infoLabel = new Label();
        infoLabel.setPrefSize(500,500);
        infoLabel.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        infoLabel.setAlignment(Pos.CENTER); 
    }

    public void voiceInputScreen() {
        response.setText(MEAL_PROMPT);

        //Record user's response
        micButton.setOnAction(e -> {
            try {
                footer.toggleRecord();
                if(footer.getRecordStatus())
                    aRecord.startRecording();
                else{
                    aRecord.stopRecording();
                    if (recordingIntoList) {
                        mealList = getVoiceInput();
                        infoLabel.setText(RESPONSE + mealList);
                    } else {
                        mealType = getVoiceInput();
                        response.setText(RESPONSE + mealType);
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        nextButton.setOnAction(e -> {
            recordingIntoList = !recordingIntoList;
            if (recordingIntoList) {
                infoLabel.setText(INGREDIENT_PROMPT);
            } else {
                response.setText(MEAL_PROMPT);
            }
        });

        doneButton.setOnAction(e -> {
            try {
                prc = new postRecipeCreate(mealType, mealList);
                prc.postRecipeCreateDisplay();
                inputStage.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        });

        this.getChildren().addAll(response, footer, recordingLabel, infoLabel);
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

// the names of these enums are shown in UI so should be nice and not programmery. If changed have to update
enum RecipeKind {
    Breakfast,
    Lunch,
    Dinner
}

class Recipe {
    public RecipeKind kind;
    public String name = "";
    public String description = "";
    
}

// JavaFX Application main entry point
public class App extends Application {

    private newScreen ns;

  public static void main(String[] args) {
        launch(args);
    }

    private ArrayList<Recipe> recipes;
    private VBox recipesUI;

    private void addRecipe(Recipe recipe) {
            recipes.add(recipe);
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
                Label title = new Label(recipe.name);
                title.setAlignment(Pos.CENTER_LEFT);
                BorderPane.setAlignment(title, Pos.CENTER_LEFT);
                descInside.setLeft(title);

                // recipe type
                Label recipeType = new Label(recipe.kind.name());
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
                    recipes.remove(recipe);
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
        recipes = new ArrayList<Recipe>();
        recipesUI.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < 100; i++) {
            Recipe toAdd = new Recipe();
            toAdd.kind = RecipeKind.values()[i % 3];
            toAdd.name = "Recipe #" + i;
            // for deleting recipes you probably want to store each recipe's UI object in the Recipe object and call delete through there
            
            addRecipe(toAdd);
        }
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