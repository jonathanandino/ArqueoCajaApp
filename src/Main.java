import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import javafx.scene.image.Image;

public class Main extends Application {

    private final LinkedHashMap<Integer, TextField> inputFields = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Label> subtotalLabels = new LinkedHashMap<>();
    private final Label totalLabel = new Label("Total: $ 0");

    private static final int[] DENOMINACIONES = {
            2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1
    };

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icono.png")));

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);

        // --- Titulo ---
        Label title = new Label("ARQUEO DE CAJA");
        title.setFont(Font.font("Arial Black", 36));
        title.setTextFill(Color.WHITE);


        // --- Grid de denominaciones ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < DENOMINACIONES.length; i++) {
            int valor = DENOMINACIONES[i];

            Label label = new Label("$ " + valor + ":");
            label.getStyleClass().add("label");
            label.setPrefWidth(90);

            TextField input = new TextField();
            input.setPromptText("Cantidad");
            input.getStyleClass().add("text-field");
            input.setPrefWidth(100);

            Label subtotal = new Label("$ 0");
            subtotal.setTextFill(Color.web("#aaaaaa"));
            subtotal.setFont(Font.font(14));
            subtotal.setPrefWidth(100);

            input.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    input.setText(newVal.replaceAll("[^\\d]", ""));
                }
                actualizarSubtotal(valor);
                actualizarTotal();
            });

            inputFields.put(valor, input);
            subtotalLabels.put(valor, subtotal);

            grid.add(label, 0, i);
            grid.add(input, 1, i);
            grid.add(subtotal, 2, i);
        }

        // --- Caja para el total ---
        HBox totalBox = new HBox(totalLabel);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setPadding(new Insets(15));
        totalBox.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 10;");
        totalLabel.setFont(Font.font("Arial", 24));
        totalLabel.setTextFill(Color.web("#f28500"));

        // --- Botón imprimir ---
        Button imprimirBtn = new Button("🖨 Imprimir Ticket");
        imprimirBtn.getStyleClass().add("button");
        imprimirBtn.setOnAction(e -> imprimirTicket());

        VBox centerBox = new VBox(30, title, grid, totalBox, imprimirBtn);
        centerBox.setAlignment(Pos.CENTER);

        root.getChildren().add(centerBox);

        Scene scene = new Scene(root, 650, 820);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Arqueo de Caja");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void actualizarSubtotal(int valor) {
        String text = inputFields.get(valor).getText();
        int cantidad = text.isEmpty() ? 0 : Integer.parseInt(text);
        int subtotal = valor * cantidad;
        subtotalLabels.get(valor).setText("$ " + subtotal);
    }

    private void actualizarTotal() {
        int total = 0;
        for (int valor : inputFields.keySet()) {
            String text = inputFields.get(valor).getText();
            int cantidad = text.isEmpty() ? 0 : Integer.parseInt(text);
            total += valor * cantidad;
        }
        totalLabel.setText("Total: $ " + total);
    }

    private void imprimirTicket() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("         *** ARQUEO DE CAJA ***\n");
        contenido.append("Fecha: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        contenido.append("----------------------------------\n");
        contenido.append(String.format("%-8s %-8s %-10s\n", "Denom", "Cant", "Subtotal"));
        contenido.append("----------------------------------\n");

        int total = 0;
        for (int valor : inputFields.keySet()) {
            String texto = inputFields.get(valor).getText();
            int cantidad = texto.isEmpty() ? 0 : Integer.parseInt(texto);
            int subtotal = valor * cantidad;
            if (cantidad > 0) {
                contenido.append(String.format("%-8s %-8s %-10s\n", "$" + valor, cantidad, "$" + subtotal));
                total += subtotal;
            }
        }

        contenido.append("----------------------------------\n");
        contenido.append(String.format("%-18s %s\n", "TOTAL:", "$" + total));
        contenido.append("\n");

        TextArea ticketArea = new TextArea(contenido.toString());
        ticketArea.setFont(Font.font("Courier New", 10));
        ticketArea.setWrapText(false);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(ticketArea);
            if (success) {
                job.endJob();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
