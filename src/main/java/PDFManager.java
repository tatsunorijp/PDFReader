import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tatsunori on 02/07/2018.
 */
public class PDFManager {
    private PDFTextStripper pdfStripper;
    private PDDocument pdDoc ;
    public String text;
    private String filePath;
    private ArrayList<String> references = new ArrayList<String>();

    public PDFManager() {

    }

    //ler o pdf
    public ArrayList<String> ToText() throws IOException{
        this.pdfStripper = null;
        this.pdDoc = new PDDocument();

        pdDoc = PDDocument.load(new File(filePath));
        pdfStripper = new PDFTextStripper();
        pdDoc.getNumberOfPages();
        pdfStripper.setStartPage(1);
        pdfStripper.setEndPage(pdDoc.getNumberOfPages());

        //copia o pdf para a memoria
        text = pdfStripper.getText(pdDoc);

        //separa o texto por linhas
        ArrayList<String> textLine = new ArrayList<String>(Arrays.asList(text.split("\n")));

        //String textAux[] = text.split("\n");
        //textLine = textAux;

        return textLine;
    }

    //retorna referencias
    public void getReferences(ArrayList<String> textLine){
        int i=0, j=0;
        String line = "";
        for(i=0; i<textLine.size(); i++){
            if(textLine.get(i).contains("REFERENCES")){
                for(j = i; j<textLine.size(); j++){
                    if(textLine.get(j).contains("[")){
                        while (!(textLine.get(j).contains("0"+".") || textLine.get(j).contains("1"+".") ||
                                textLine.get(j).contains("2"+".") || textLine.get(j).contains("3"+".")||
                                textLine.get(j).contains("4"+".") || textLine.get(j).contains("5"+".")||
                                textLine.get(j).contains("6"+".") || textLine.get(j).contains("7"+".")||
                                textLine.get(j).contains("8"+".") || textLine.get(j).contains("9"+".")||
                                textLine.get(j).contains("www"))){

                            line += textLine.get(j);
                            j++;
                        }
                        line += textLine.get(j);
                        i=j;
                        StaticVariables.references += (line + "\n");
                        StaticVariables.quantReferences++;
                        line = "";
                    }

                }
            }
        }
    }


    public String getAuthor(ArrayList<String> textLine){
        CRFClassifier<CoreMap> crf = CRFClassifier.getDefaultClassifier();

        for(String linha: textLine){
            String classifiedString = crf.classifyToString(linha);
            //System.out.println(classifiedString);
            if(classifiedString.contains("PERSON")){
                StaticVariables.autors = linha;
                StaticVariables.quantAutors = Integer.toString((StringUtils.countMatches(classifiedString, "PERSON"))/2);
                System.out.println(classifiedString);
                return linha;
            }
        }
        return null;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
