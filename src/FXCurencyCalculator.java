import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class FXCurencyCalculator extends Application{

    Map<String, Double> exchangeRates = new HashMap<>();
    String toCurency;
    int lastConversionDirection;

    private void readExchangeRates() throws Exception{
        try (BufferedReader br = new BufferedReader(new FileReader(
                "C:\\Users\\lanev_000\\IdeaProjects\\FXCurencyCalculator-DL\\src\\ExchangeRates"))){
            String sLine;
            while ((sLine = br.readLine()) != null){
                String[] exchangeParts = sLine.split(" ");
                exchangeRates.put(exchangeParts[0], Double.parseDouble(exchangeParts[1]));
            }
        }
    }

    private String conversion(String toCurency, String toConvert, int direction){
        try{
            if (direction == 0){
                Double converted = Double.parseDouble(toConvert) * exchangeRates.get(toCurency);
                return String.format("%.3f",converted);
            }
            else if (direction == 1){
                Double converted = Double.parseDouble(toConvert) / exchangeRates.get(toCurency);
                return String.format("%.3f",converted);
            }
            else {
                return "0";
            }
        } catch (NumberFormatException ex){
            return "0";
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER);

        //Vertical box for vertical alignment control
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.TOP_CENTER);
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);

        //EUR
        VBox EURBox = new VBox(10);
        EURBox.setAlignment(Pos.CENTER);
        Label EUR = new Label("EUR");
        EUR.setAlignment(Pos.CENTER);
        TextField textEUR = new TextField("0.000");
        textEUR.setAlignment(Pos.CENTER);

        EURBox.getChildren().addAll(EUR,textEUR);

        //Dummy vertical box to put "to" between text boxes.
        VBox DBox = new VBox(10);
        Label dummy = new Label("");
        EUR.setAlignment(Pos.CENTER);
        Label toText = new Label("to");
        EUR.setAlignment(Pos.CENTER);

        DBox.getChildren().addAll(dummy,toText);

        //Other Currency
        VBox OCBox = new VBox(10);
        OCBox.setAlignment(Pos.CENTER);
        Label otherCurrency = new Label("Other currency");
        otherCurrency.setAlignment(Pos.CENTER);
        TextField textOtherCurrency = new TextField("0.000");
        textOtherCurrency.setAlignment(Pos.CENTER);

        OCBox.getChildren().addAll(otherCurrency,textOtherCurrency);

        //Conversion options choicebox.
        ChoiceBox<String> conversionChoice = new ChoiceBox<>();
        conversionChoice.setPrefWidth(410);
        readExchangeRates();
        for (String key : exchangeRates.keySet()) {
            conversionChoice.getItems().add("EUR to "+key);
        }
        conversionChoice.getSelectionModel().selectFirst();
        toCurency = conversionChoice.getSelectionModel().getSelectedItem().split("EUR to ")[1];
        otherCurrency.setText(toCurency);

        //Clear button
        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(410);

        //Logo
        Canvas logoCanvas = new Canvas(410, 40);
        GraphicsContext logoContext = logoCanvas.getGraphicsContext2D();
        logoContext.setFill(Color.BLACK);
        logoContext.fillPolygon(new double[]{0, 0, 20}, new double[]{0, 30, 30}, 3);
        logoContext.fillOval(15, 0, 30,30);
        logoContext.setFill(Color.RED);
        logoContext.fillArc(46,0,30,30,90,270, ArcType.ROUND);
        logoContext.setFill(Color.BLACK);
        logoContext.fillOval(77, 0, 30,30);

        //All to pane
        hBox.getChildren().addAll(EURBox,DBox,OCBox);
        vBox.getChildren().addAll(hBox,conversionChoice,clearButton, logoCanvas);
        pane.getChildren().add(vBox);

        Scene scene = new Scene(pane,460,200);
        primaryStage.setScene(scene);
        primaryStage.show();

        //TextBox event handlers
        textEUR.setOnKeyPressed(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    textOtherCurrency.setText(conversion(toCurency,textEUR.getText(),0));
                    lastConversionDirection = 0;
                }
            }
        });

        textOtherCurrency.setOnKeyPressed(new EventHandler<KeyEvent>(){
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    textEUR.setText(conversion(toCurency,textOtherCurrency.getText(),1));
                    lastConversionDirection = 1;
                }
            }
        });

        //Selection event handler
        conversionChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                conversionChoice.getSelectionModel().clearAndSelect(newValue.intValue());
                toCurency = conversionChoice.getSelectionModel().getSelectedItem().split("EUR to ")[1];
                otherCurrency.setText(toCurency);

                if (lastConversionDirection == 0){
                    textOtherCurrency.setText(conversion(toCurency,textEUR.getText(),0));
                }
                else if (lastConversionDirection == 1){
                    textEUR.setText(conversion(toCurency,textOtherCurrency.getText(),1));
                }
            }
        });

        clearButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                textEUR.setText("0.000");
                textOtherCurrency.setText("0.000");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
