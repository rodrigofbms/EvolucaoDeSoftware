package Regex;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Integer totalDeLinhasTotal = 0;
        Integer totalDeMetodosTotal = 0;
        Integer totalDeClassesTotal = 0;
        Integer totalDeClassesDeusesTotal = 0;
        Integer totalDeMetodosDeusesTotal = 0;


        Pattern patternComentarios = Pattern.compile(regexCOmentarios);
        Pattern patternClasse = Pattern.compile(regexClasse);
        Pattern patternMetodo = Pattern.compile(regexMetodo);


        String texto = "";

        //Criacao do arquivo CSV 27 meses
        Writer writer = Files.newBufferedWriter(Paths.get("resultado.csv"));

        //Escrevendo o cabecalho do CSV 27 meses
        writer.append("Mes" + ",");
        writer.append("LOC" + ",");
        writer.append("Classes" + ",");
        writer.append("Metodos" + ",");
        writer.append("Classes Deuses" + ",");
        writer.append("Metodos Deuses" + "\n");

        //Criacao do arquivo CSV 28 meses
        Writer writer2 = Files.newBufferedWriter(Paths.get("predicao.csv"));

        //Escrevendo o cabecalho do CSV 28 meses
        writer2.append("Mes" + ",");
        writer2.append("LOC" + ",");
        writer2.append("Classes" + ",");
        writer2.append("Metodos" + ",");
        writer2.append("Classes Deuses" + ",");
        writer2.append("Metodos Deuses" + "\n");

        Integer [] totalDeLinhasMesArray = new Integer[28];
        Integer [] totalDeClassesMesArray = new Integer[28];
        Integer [] totalDeMetodosMesArray = new Integer[28];
        Integer [] totalDeClassesDeusesMesArray = new Integer[28];
        Integer [] totalDeMetodosDeusesMesArray = new Integer[28];

        //Inicializando os valores dos arrays
        for (int t = 1; t < totalDeClassesMesArray.length; t++){
            totalDeLinhasMesArray[t] = 0;
            totalDeClassesMesArray[t] = 0;
            totalDeMetodosMesArray[t] = 0;
            totalDeClassesDeusesMesArray[t] = 0;
            totalDeMetodosDeusesMesArray[t] = 0;
        }

        int i = 1;

        for (; i <= 27; i++) {
            System.out.println("=================Mes " + i + " =================");

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

                //Verifica a contagem de linhas, classes, metodos, classes deuses e metodos deuses
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
                            totalDeLinhasClasse = 0;
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
                                totalDeLinhasMetodo = 0;
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
                totalDeLinhasMesArray [i] += totalDeLinhas;
                totalDeClassesMesArray [i] += totalDeClasses;
                totalDeMetodosMesArray [i] += totalDeMetodos;
                totalDeClassesDeusesMesArray [i] += totalDeClassesDeuses;
                totalDeMetodosDeusesMesArray [i] += totalDeMetodosDeuses;



            }

            //Adicionando os valores totais de cada variavel
            totalDeLinhasTotal += totalDeLinhasMesArray [i];
            totalDeClassesTotal += totalDeClassesMesArray [i];
            totalDeMetodosTotal += totalDeMetodosMesArray [i];
            totalDeClassesDeusesTotal += totalDeClassesDeusesMesArray [i];
            totalDeMetodosDeusesTotal += totalDeMetodosDeusesMesArray [i];

            //Adicionando o resultado do mes no CSV de 27 meses
                writer.append(i + ",");
                writer.append(totalDeLinhasMesArray[i] + ",");
                writer.append(totalDeClassesMesArray[i] + ",");
                writer.append(totalDeMetodosMesArray[i] + ",");
                writer.append(totalDeClassesDeusesMesArray[i] + ",");
                writer.append(totalDeMetodosDeusesMesArray[i] + "\n");

            //Adicionando o resultado do mes no CSV de 28 meses
            writer2.append(i + ",");
            writer2.append(totalDeLinhasMesArray[i] + ",");
            writer2.append(totalDeClassesMesArray[i] + ",");
            writer2.append(totalDeMetodosMesArray[i] + ",");
            writer2.append(totalDeClassesDeusesMesArray[i] + ",");
            writer2.append(totalDeMetodosDeusesMesArray[i] + "\n");


        }

        //Pegando os valores da media aritmetica de cada variavel
        Integer mediaAritmeticaLOC = mediaAritmetica(totalDeLinhasTotal,i);
        Integer mediaAritmeticaClasses = mediaAritmetica(totalDeClassesTotal,i);
        Integer mediaAritmeticaMetodos = mediaAritmetica(totalDeMetodosTotal,i);
        Integer mediaAritmeticaClassesDeuses = mediaAritmetica(totalDeClassesDeusesTotal,i);
        Integer mediaAritmeticaMetodosDeuses = mediaAritmetica(totalDeMetodosDeusesTotal,i);


        //Desvio padrao de cada variavel
        int desvioPadraoLOC = 0;
        int desvioPadraoClasses = 0;
        int desvioPadraoMetodos = 0;
        int desvioPadraoClassesDeuses = 0;
        int desvioPadraoMetodosDeuses = 0;

        //Predicao dos valores do 28 mes
        desvioPadraoLOC = desvioPadrao(totalDeLinhasMesArray, mediaAritmeticaLOC, i);
        desvioPadraoMetodos = desvioPadrao(totalDeMetodosMesArray, mediaAritmeticaMetodos, i);
        desvioPadraoClasses = desvioPadrao(totalDeClassesMesArray, mediaAritmeticaClasses, i);
        desvioPadraoClassesDeuses = desvioPadrao(totalDeClassesDeusesMesArray, mediaAritmeticaClassesDeuses, i);
        desvioPadraoMetodosDeuses = desvioPadrao(totalDeMetodosDeusesMesArray, mediaAritmeticaMetodosDeuses, i);


        //Adicionando a predicao do 28 mes no CSV de 28 meses
        writer2.append(i + ",");
        writer2.append(mediaAritmeticaLOC - desvioPadraoLOC + ",");
        writer2.append(mediaAritmeticaClasses - desvioPadraoClasses + ",");
        writer2.append(mediaAritmeticaMetodos - desvioPadraoMetodos + ",");
        writer2.append(mediaAritmeticaClassesDeuses - desvioPadraoClassesDeuses + ",");
        writer2.append(mediaAritmeticaMetodosDeuses - desvioPadraoMetodosDeuses + "\n");

        //Escreve nos arquivos CSVs de 27 meses e de 28 meses os resultados obtidos
        writer.flush();
        writer2.flush();
        writer.close();
        writer2.close();
    }
    public static int mediaAritmetica(int total, int totalDeMeses){
        return total/(totalDeMeses - 1);
    }

    public static int desvioPadrao(Integer [] array, int mediaAritmetica, int totalDeMeses) {
        double valorAoQuadrado = 0;
        double somatorioDesvioPadrao = 0;
        double desvioPadrao = 0;
        for (int f = 1; f < array.length; f++) {
            valorAoQuadrado = Math.pow((array[f] - mediaAritmetica), 2);
            somatorioDesvioPadrao += valorAoQuadrado;
        }
        desvioPadrao = Math.sqrt((somatorioDesvioPadrao/(totalDeMeses - 1)));
        int desvioPadraoInt = (int)desvioPadrao;

        return desvioPadraoInt;
    }

}
