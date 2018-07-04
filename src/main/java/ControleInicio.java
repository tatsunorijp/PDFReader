import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tatsunori on 02/07/2018.
 */
public class ControleInicio {
    @FXML
    JFXTextArea taReferencias;
    @FXML
    JFXTextArea taAutor;
    @FXML
    JFXTextArea taTermos;
    @FXML
    JFXButton btBuscar;
    @FXML
    JFXButton btLimpar;
    @FXML
    JFXTextField tfArquivo;

    String path = "C:/Users/Tatsunori/Downloads/IA2/src/com/company/files/";
    PDFManager pdfManager = new PDFManager();
    ArrayList<String> text;
    ArrayList<String> references;
    String autors;

    public void setBtBuscar() throws IOException {
        text = new ArrayList<String>();
        references = new ArrayList<String>();
        autors = new String();
        //open archive
        path += tfArquivo.getText();
        pdfManager.setFilePath(path);
        text = pdfManager.ToText();

        //find autors
        pdfManager.getAuthor(text);
        taAutor.setText(autors);
    }

    public void setBtLimpar(){
        taReferencias.setText("");
        taAutor.setText("");
        taTermos.setText("");
    }

}
