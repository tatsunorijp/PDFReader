import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tatsunori on 03/07/2018.
 */
public class ControleStart {
    @FXML
    TextArea taReferencias;
    @FXML
    JFXTextField tfAutor;
    @FXML
    TableView tvTermos;
    @FXML
    JFXButton btSearch;
    @FXML
    JFXButton btInformation;
    @FXML
    JFXTextField tfArquivo;
    @FXML
    Label lbAutors;
    @FXML
    Label lbReferences;

    String path = "";
    PDFManager pdfManager = new PDFManager();
    ArrayList<String> text;

    public void setBtSearch() throws IOException {
        //reseta o contador de referencias
        StaticVariables.quantReferences = 1;
        StaticVariables.references = "";
        taReferencias.setText("");

        text = new ArrayList<String>();

        //open archive
        path =  "src/main/files/" + tfArquivo.getText();
        pdfManager.setFilePath(path);
        text = pdfManager.ToText();

        //find autors
        pdfManager.getAuthor(text);
        tfAutor.setText(StaticVariables.autors);
        lbAutors.setText(StaticVariables.quantAutors);

        //find references
        pdfManager.getReferences(text);
        taReferencias.setText(StaticVariables.references);
        lbReferences.setText(Integer.toString(StaticVariables.quantReferences));
    }

    public void setBtInformations(){
        taReferencias.setText("");
        tfAutor.setText("");
        //tvTermos.setText("");
    }

}
