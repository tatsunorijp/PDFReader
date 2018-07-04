import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Tatsunori on 03/07/2018.
 */
public class Arquivo {

    public static void exportarDados(String arquivo) throws IOException {
        String nome = arquivo.replace("pdf", "txt");
        FileWriter arq = new FileWriter("src/main/files/" + nome);
        PrintWriter gravaArq = new PrintWriter(arq);

        gravaArq.printf("AUTORES: %n");
        gravaArq.printf(StaticVariables.autors + ";");
        gravaArq.printf("%n");
        gravaArq.printf("REFERENCIAS: %n");
        gravaArq.printf(StaticVariables.references);
        gravaArq.printf("%n");

        for(Termo t: StaticVariables.termosEstatico){
            gravaArq.printf(t.getName() + ", " + t.getQty() + ";" + "%n");
        }

        arq.close();

    }
}
