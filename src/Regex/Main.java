package Regex;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        String regexCOmentarios = "\\/\\*[\\w\\W]*?\\*\\/|\\/\\/.*|\\/\\*.*|\\*\\/|^\\s*\\*.*";
        String regexClasse = "\\s+class\\s+";
        String regexMetodo = "public.+\\([^;=*{]*\\)\\s*\\{|private.+\\([^;=*{]*\\)\\s*\\{|protected.+\\([^;=*{]*\\)\\s*\\{";
        String arquivos[] = new String[6];
        arquivos[0] = "DispatchQueue.txt";
        arquivos[1] = "FileLoader.txt";
        arquivos[2] = "FileLog.txt";
        arquivos[3] = "FileUploadOperation.txt";
        arquivos[4] = "UserConfig.txt";
        arquivos[5] = "Utilities.txt";
        Integer parentesesMetodo = 0;
        Integer totalDeLinhas = 0;
        Integer totalDeClasses = 0;
        Integer totalDeMetodos = 0;
        Integer totalDeMetodosDeuses = 0;
        Integer totalDeClassesDeuses = 0;
        Integer totalDeLinhasMetodo = 0;
        Integer totalDeLinhasClasse = 0;
        Integer totalDeLinhasMes = 0;
        Integer totalDeClassesMes = 0;
        Integer totalDeMetodosMes = 0;
        Integer totalDeMetodosDeusesMes = 0;
        Integer totalDeClassesDeusesMes = 0;


        Pattern patternComentarios = Pattern.compile(regexCOmentarios);
        Pattern patternClasse = Pattern.compile(regexClasse);
        Pattern patternMetodo = Pattern.compile(regexMetodo);


        String texto = "";

        //Criacao do arquivo CSV
        Writer writer = Files.newBufferedWriter(Paths.get("resultado.csv"));
        //Escrevendo o cabecalho do CSV
        writer.append("Mes" + ",");
        writer.append("LOC" + ",");
        writer.append("Classes" + ",");
        writer.append("Metodos" + ",");
        writer.append("Classes Deuses" + ",");
        writer.append("Metodos Deuses" + "\n");


        for (int i = 1; i <= 27; i++) {
            System.out.println("=================Mes " + i + " =================");
            //Resetar os valores do contador de cada mes
            totalDeClassesMes = 0;
            totalDeLinhasMes = 0;
            totalDeMetodosMes = 0;
            totalDeMetodosDeusesMes = 0;
            totalDeClassesDeusesMes = 0;

            for (int a = 0; a < 6; a++) {

                //Resetar os valores do contador a cada entrada em cada respectivo arquivo
                totalDeClasses = 0;
                totalDeLinhas = 0;
                totalDeMetodos = 0;
                totalDeClassesDeuses = 0;
                totalDeMetodosDeuses = 0;
                totalDeLinhasClasse = 0;

                //Pegar o diretorio do dataset
                Path path = Paths.get("Dataset/" + i + "/" + arquivos[a]);

                //Leitura do arquivo para retirar os comentarios e os espacos
                texto = Files.readString(path, StandardCharsets.UTF_8);
                texto = texto.replaceAll("\\/\\*[\\w\\W]*?\\*\\/|\\/\\/.*|\\/\\*.*|\\*\\/|^\\s*\\*.*"," ");
                texto = texto.trim();

                //Gravar o arquivo que foi retirado os comentarios
                FileWriter diretorio = new FileWriter(String.valueOf(path));
                PrintWriter gravarArquivo = new PrintWriter(diretorio);
                gravarArquivo.write(texto);
                gravarArquivo.close();



                //Pegar o arquivo modificado e aplicar o Regex
                BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)));
                System.out.println("-----Diretorio " + String.valueOf(path) +"-----" );

                //Verifica a contagem de linhas, classes e metodos
                while ((texto = br.readLine()) != null) {

                    Matcher matcherComentarios = patternComentarios.matcher(texto);
                    Matcher matcherMetodo = patternMetodo.matcher(texto);
                    Matcher matcherClasse = patternClasse.matcher(texto);


                    if (!matcherComentarios.find() && !texto.isEmpty()) {

                        totalDeLinhas++;

                    }

                    if (matcherClasse.find()) {
                        totalDeLinhasClasse = 0;
                        totalDeClasses++;
                        totalDeLinhasClasse++;

                    }else if(!texto.isEmpty()){
                        totalDeLinhasClasse++;
                        if(totalDeLinhasClasse > 800){
                            totalDeClassesDeuses++;
                        }
                    }
                    if (matcherMetodo.find()) {
                        totalDeLinhasMetodo = 0;
                        parentesesMetodo = 0;
                        totalDeLinhasMetodo++;
                        parentesesMetodo++;
                        totalDeMetodos++;

                    }else if (!texto.isEmpty()){
                        totalDeLinhasMetodo++;
                        if (texto.contains("{")) {
                            parentesesMetodo++;
                        }
                        if (texto.contains("}")) {
                            parentesesMetodo--;
                        }
                        if (parentesesMetodo == 0) {
                            if (totalDeLinhasMetodo > 127) {
                                totalDeMetodosDeuses++;
                            }
                        }
                    }

                }
                System.out.println("Quantidade Total de Linhas de Código = " + totalDeLinhas);
                System.out.println("Quantidade Total de Classes no Código = " + totalDeClasses);
                System.out.println("Quantidade Total de Métodos no Código = " + totalDeMetodos);
                System.out.println("Quantidade Total de Métodos Deuses = " + totalDeMetodosDeuses);
                System.out.println("Quantidade Total de Classes Deuses = " + totalDeClassesDeuses);


                //Adicionando os valores de cada respectivo arquivo ao valor total do mes
                totalDeLinhasMes += totalDeLinhas;
                totalDeClassesMes += totalDeClasses;
                totalDeMetodosMes += totalDeMetodos;
                totalDeMetodosDeusesMes += totalDeMetodosDeuses;
                totalDeClassesDeusesMes += totalDeClassesDeuses;

            }

            //Adicionando o resultado do mes no CSV
            writer.append(i +",");
            writer.append(totalDeLinhasMes + ",");
            writer.append(totalDeClassesMes + ",");
            writer.append(totalDeMetodosMes +",");
            writer.append(totalDeClassesDeusesMes +",");
            writer.append(totalDeMetodosDeusesMes +"\n");
        }
        //Escreve no arquivo CSV os resultados obtidos
        writer.flush();
        writer.close();

    }

}
