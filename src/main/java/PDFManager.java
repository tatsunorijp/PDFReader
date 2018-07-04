import com.google.common.base.Joiner;
import com.uttesh.exude.ExudeData;
import com.uttesh.exude.exception.InvalidDataException;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public List<Termo> getCommonWords(ArrayList<String> textline) throws InvalidDataException {

        HashMap<String, Integer> wordmap = new HashMap<String, Integer>();
        HashMap<String, Integer> filteredwordmap = new HashMap<String, Integer>();
        ArrayList<Termo> retval = new ArrayList<Termo>();

        /*for(String aux : textline){
            System.out.println(aux);
        }*/

        //mapeando todas as palavras que aparecem no pdf
        for(String line: textline){
            if(line.contains("REFERENCES")) break;
            for (String word : line.split("\\s+")){
                word = word.replaceAll("[^\\w]", "");
                if(wordmap.get(word)==null){
                    wordmap.put(word, 1);
                } else{
                    wordmap.put(word, wordmap.get(word) + 1);
                }
            }
        }

        // imprime o mapa de palavras poluido
        /*for (HashMap.Entry<String, Integer> entry : wordmap.entrySet()) {

            System.out.println("word: " + entry.getKey() + "    times: " + entry.getValue());

        }*/

        //concatenando textline em uma unica string para filtragem
        String textbuffer = Joiner.on("\n").join(textline);

        //realizando a filtragem de termos
        String filteredbuffer = ExudeData.getInstance().filterStoppings(textbuffer);

        System.out.println(filteredbuffer);

        //novo mapa que contém apenas palavras nao stopwords
        for (String word : filteredbuffer.split("\\s+")){
            if(wordmap.get(word)!=null){
                filteredwordmap.put(word, wordmap.get(word));
            }
        }

        //imprime o mapa de palavras filtrado
        /*for (HashMap.Entry<String, Integer> entry : filteredwordmap.entrySet()) {

            System.out.println("word: " + entry.getKey() + "\t\t\t\ttimes: " + entry.getValue());

        }*/

        //ordenando o mapa por quantidade de vezes que apareceu
        Set<HashMap.Entry<String, Integer>> set = filteredwordmap.entrySet();

        List<HashMap.Entry<String, Integer>> list = new ArrayList<HashMap.Entry<String, Integer>>(set);

        Collections.sort(list, new Comparator<HashMap.Entry<String, Integer>>() {

            @Override
            public int compare(HashMap.Entry<String, Integer> o1,
                               HashMap.Entry<String, Integer> o2) {

                return o2.getValue().compareTo(o1.getValue());
            }

        });

        for(int i=0; i<10; i++){
            retval.get(i).setName(list.get(0).getKey());
            retval.get(i).setQty(list.get(0).getValue());
        }

        return retval;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
