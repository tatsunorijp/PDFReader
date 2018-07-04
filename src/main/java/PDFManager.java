import com.google.common.base.Joiner;
import com.uttesh.exude.ExudeData;
import com.uttesh.exude.exception.InvalidDataException;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


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

        return textLine;
    }

    //retorna referencias
    public void getReferences(ArrayList<String> textLine){
        String line = "";
        int indexStart, indexEnd, maiorNum = 0;
        String aux;

        for(int i=0; i<textLine.size(); i++){
            if(textLine.get(i).contains("REFERENCES")){
                for(int j = i; j<textLine.size(); j++){
                    if(textLine.get(j).contains("[")){

                        //descobre o numero da ultima referencia computada
                        indexStart = textLine.get(j).indexOf("[");
                        indexEnd = textLine.get(j).indexOf("]");
                        aux = textLine.get(j).substring(indexStart+1, indexEnd);
                        if(maiorNum < Integer.parseInt(aux)){
                            maiorNum = Integer.parseInt(aux);
                        }

                        while (!(textLine.get(j).contains("0"+".") || textLine.get(j).contains("1"+".") ||
                                textLine.get(j).contains("2"+".") || textLine.get(j).contains("3"+".")||
                                textLine.get(j).contains("4"+".") || textLine.get(j).contains("5"+".")||
                                textLine.get(j).contains("6"+".") || textLine.get(j).contains("7"+".")||
                                textLine.get(j).contains("8"+".") || textLine.get(j).contains("9"+".")||
                                textLine.get(j).contains("www"))){

                            //concatena as linhas de uma referencia
                            line += textLine.get(j);
                            j++;
                        }
                        //concatena a ultima linha da atual referencia
                        line += (textLine.get(j) + ";");
                        i=j;

                        //adocopma a mpva referemcoa ma ;omja de baixo da variavel estatica
                        StaticVariables.references += (line + "\n");
                        //salva a quantidade de referencias
                        StaticVariables.quantReferences = maiorNum;
                        //reseta a variavel para pegar a proxima referencia
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
            if(classifiedString.contains("PERSON")){
                StaticVariables.autors = linha;
                StaticVariables.quantAutors = Integer.toString((StringUtils.countMatches(classifiedString, "PERSON"))/2);
                return linha;
            }
        }
        return null;
    }

    public ArrayList<Termo> getCommonWords(ArrayList<String> textline) throws InvalidDataException {

        HashMap<String, Integer> filteredwordmap = new HashMap<String, Integer>();
        ArrayList<Termo> retval = new ArrayList<Termo>();
        String filteredbuffer = "";

        //filtrar as stopwords do pdf
        for(String line : textline){
            if(line.contains("REFERENCES")) break;
            filteredbuffer += ExudeData.getInstance().filterStoppingsKeepDuplicates(line);
        }
        //System.out.println(filteredbuffer);

        //contar as ocorrencias de cada palavra
        for (String word : filteredbuffer.split("\\s+")){
            if(word.length()<3) continue;
            if(filteredwordmap.get(word)==null){
                filteredwordmap.put(word, 1);
            } else{
                filteredwordmap.put(word, filteredwordmap.get(word) + 1);
            }
        }

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
            retval.add(new Termo(list.get(i).getKey(), list.get(i).getValue()));
            //System.out.println(retval.get(i).getName() + "    " + retval.get(i).getQty());
        }

        return retval;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
