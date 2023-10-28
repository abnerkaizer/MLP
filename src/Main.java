import java.util.List;
import java.util.ArrayList;
public class Main {
    private static double[][][] classe1 = new double[288][2][];
    private static double[][][] classe2 = new double [49][2][];
    private static double[][][] classe3 = new double[288][2][];
    private static List<double[][][]> classes = new ArrayList<>(3);
    private static List<ArrayList<Double>> medias = new ArrayList<>(3);
    private static List<ArrayList<Double>> desvios = new ArrayList<>(3);

    public static void main(String[] args) {
        setBases();
        double startTime = System.currentTimeMillis();
        //execute(10000);
        classificadorBayes();
        double endTime = System.currentTimeMillis();
        double tempo = (endTime-startTime)/1000;
        System.out.printf("Tempo: %.2f s\n",tempo);
        
    }
    private static void classificadorBayes(){
        double classe [][][];

        for (int j = 0; j < classes.size(); j++) {
            classe = classes.get(j);
            ArrayList<Double> media = new ArrayList<>(4);
            ArrayList<Double> desvio = new ArrayList<>(4);

            //Treino
            double entradas [];
            for (int i = 0; i < 4; i++) {
                double soma = 0;
                for (int k = 0; k <(int) classe.length*0.7; k++) {
                    entradas = classe[k][0];
                    soma += entradas[i];
                }
                media.add(soma/(int) classe.length*0.7);
            }
            medias.add(media);
            for (int i = 0; i < 4; i++) {
                double soma = 0;
                for (int k = 0; k < (int) classe.length*0.7; k++) {
                    entradas = classe[k][0];
                    soma += Math.pow(entradas[i]-media.get(i),2);
                }
                desvio.add(Math.sqrt(soma/(int) classe.length*0.7));
            }
            desvios.add(desvio);
        }

        //imprimeMatriz(medias);
        //imprimeMatriz(desvios);
        //Teste
        
    }
    private static void imprimeMatriz(List<ArrayList<Double>> matriz){
        //System.out.println(matriz.get(0).size());

        for (int i = 0; i < matriz.size(); i++) {
            System.out.println("Classe "+(i+1)+":");
            for (int j = 0; j < matriz.get(i).size(); j++) {
                System.out.print(matriz.get(i).get(j)+" ");
            }
            System.out.println();
            System.out.println();
        }
    }
    private static void execute(int epocas) {
        MLP mlp = new MLP(4,4,3,0.003);
        double base [][][];
        for (int e = 0; e < epocas; e++) {
            double eAproxEpocaTreino = 0;
            double eClassEpocaTreino = 0;
            double eAproxEpocaTeste = 0;
            double eClassEpocaTeste = 0;
            for (int j = 0; j < classes.size(); j++) {
                base = classes.get(j);
                //Treino
                for (int a = 0; a <(int) base.length*0.7; a++) {
                    double []x = base[a][0];
                    double []y = base[a][1];
                    List<Double> xin = new ArrayList<Double>(x.length+1);
                    for(double d:x) {
                        xin.add(d);
                    }
                    xin.add(1.0);
                    double [] theta = mlp.treinar(xin, y);
                    double eAproxAmostraTreino = 0;
                    double eClassAmostraTreino = 0;
                    for (int i = 0; i < theta.length; i++) {
                        eAproxAmostraTreino += Math.abs((y[i] - theta[i]));
                    }
                    double ot[] = getOutThreshold(theta);
                    int soma = 0;
                    for (int i = 0; i < ot.length; i++) {
                        soma += Math.abs(y[i] - ot[i]);
                    }
                    if (soma > 0) eClassAmostraTreino = 1;
                    eClassEpocaTreino += eClassAmostraTreino;
                    eAproxEpocaTreino += eAproxAmostraTreino;

                }
                //Teste
                for (int a =(int)( base.length*0.7); a < base.length; a++) {
                    double []x = base[a][0];
                    double []y = base[a][1];
                    List<Double> xin = new ArrayList<Double>(x.length+1);
                    for(double d:x) {
                        xin.add(d);
                    }
                    xin.add(1.0);
                    double [] theta = mlp.teste(xin, y);
                    double eAproxAmostraTeste = 0;
                    double eClassAmostraTreino = 0;
                    for (int i = 0; i < theta.length; i++) {
                        eAproxAmostraTeste += Math.abs((y[i] - theta[i]));
                    }
                    double ot[] = getOutThreshold(theta);
                    int soma = 0;
                    for (int i = 0; i < ot.length; i++) {
                        soma += Math.abs(y[i] - ot[i]);
                    }
                    if (soma > 0) eClassAmostraTreino = 1;
                    eClassEpocaTeste += eClassAmostraTreino;
                    eAproxEpocaTeste += eAproxAmostraTeste;
                }
            }
            System.out.println("Epoca: " + (e + 1)
                    + "\nTreino: erro de aproximação: " + eAproxEpocaTreino+" erro de classificação: "+eClassEpocaTreino
                    + " - Teste:  erro de aproximação: "+ eAproxEpocaTeste+" erro de epoca: "+ eClassEpocaTeste);
        }
    }
    private static double maior(double[]numbers){
        double maior = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i]>maior){
                maior = numbers[i];
            }
        }
        return maior;
    }
    private static double normaliza(double x){
        return (x-1)/4;
    }
    private static double [] getOutThreshold(double [] out) {
        double [] ret = new double[out.length];
        double maior = maior(out);
        for (int i = 0; i < ret.length; i++) {
            if (out[i] == maior) {
                ret[i] = 0.995;
            }else {
                ret[i] = 0.005;
            }
        }
        return ret;
    }


    private static void setBases(){
        for (int i = 0; i < 288; i++) {
            classe1[i][0] = new double[4];
            classe1[i][1] = new double[3];
            classe3[i][0] = new double[4];
            classe3[i][1] = new double[3];
        }
        for (int i = 0; i < 49; i++) {
            classe2[i][0] = new double[4];
            classe2[i][1] = new double[3];
        }
        String lines[] = input.split("\n");
        int b1=0,b2=0,b3 =0;
        for (int i = 0; i < 625; i++) {
            String c[] = lines[i].split(",");
            if (c[0].equals("L")) {
                classe1[b1][1][0] = 0.995;
                classe1[b1][1][1] = 0.005;
                classe1[b1][1][2] = 0.005;
                for (int j = 1; j < c.length; j++) {
                    classe1[b1][0][j - 1] = Double.parseDouble(c[j]);
                }
                b1++;
            } else if (c[0].equals("B")) {
                classe2[b2][1][0] = 0.005;
                classe2[b2][1][1] = 0.995;
                classe2[b2][1][2] = 0.005;
                for (int j = 1; j < c.length; j++) {
                    classe2[b2][0][j - 1] = Double.parseDouble(c[j]);
                }
                b2++;
            } else if (c[0].equals("R")) {
                classe3[b3][1][0] = 0.005;
                classe3[b3][1][1] = 0.005;
                classe3[b3][1][2] = 0.995;
                for (int j = 1; j < c.length; j++) {
                    classe3[b3][0][j - 1] = Double.parseDouble(c[j]);
                }
                b3++;
            }
        }
        classes.add(classe1);
        classes.add(classe2);
        classes.add(classe3);
    }
    static String input = "B,1,1,1,1\n" +
            "R,1,1,1,2\n" +
            "R,1,1,1,3\n" +
            "R,1,1,1,4\n" +
            "R,1,1,1,5\n" +
            "R,1,1,2,1\n" +
            "R,1,1,2,2\n" +
            "R,1,1,2,3\n" +
            "R,1,1,2,4\n" +
            "R,1,1,2,5\n" +
            "R,1,1,3,1\n" +
            "R,1,1,3,2\n" +
            "R,1,1,3,3\n" +
            "R,1,1,3,4\n" +
            "R,1,1,3,5\n" +
            "R,1,1,4,1\n" +
            "R,1,1,4,2\n" +
            "R,1,1,4,3\n" +
            "R,1,1,4,4\n" +
            "R,1,1,4,5\n" +
            "R,1,1,5,1\n" +
            "R,1,1,5,2\n" +
            "R,1,1,5,3\n" +
            "R,1,1,5,4\n" +
            "R,1,1,5,5\n" +
            "L,1,2,1,1\n" +
            "B,1,2,1,2\n" +
            "R,1,2,1,3\n" +
            "R,1,2,1,4\n" +
            "R,1,2,1,5\n" +
            "B,1,2,2,1\n" +
            "R,1,2,2,2\n" +
            "R,1,2,2,3\n" +
            "R,1,2,2,4\n" +
            "R,1,2,2,5\n" +
            "R,1,2,3,1\n" +
            "R,1,2,3,2\n" +
            "R,1,2,3,3\n" +
            "R,1,2,3,4\n" +
            "R,1,2,3,5\n" +
            "R,1,2,4,1\n" +
            "R,1,2,4,2\n" +
            "R,1,2,4,3\n" +
            "R,1,2,4,4\n" +
            "R,1,2,4,5\n" +
            "R,1,2,5,1\n" +
            "R,1,2,5,2\n" +
            "R,1,2,5,3\n" +
            "R,1,2,5,4\n" +
            "R,1,2,5,5\n" +
            "L,1,3,1,1\n" +
            "L,1,3,1,2\n" +
            "B,1,3,1,3\n" +
            "R,1,3,1,4\n" +
            "R,1,3,1,5\n" +
            "L,1,3,2,1\n" +
            "R,1,3,2,2\n" +
            "R,1,3,2,3\n" +
            "R,1,3,2,4\n" +
            "R,1,3,2,5\n" +
            "B,1,3,3,1\n" +
            "R,1,3,3,2\n" +
            "R,1,3,3,3\n" +
            "R,1,3,3,4\n" +
            "R,1,3,3,5\n" +
            "R,1,3,4,1\n" +
            "R,1,3,4,2\n" +
            "R,1,3,4,3\n" +
            "R,1,3,4,4\n" +
            "R,1,3,4,5\n" +
            "R,1,3,5,1\n" +
            "R,1,3,5,2\n" +
            "R,1,3,5,3\n" +
            "R,1,3,5,4\n" +
            "R,1,3,5,5\n" +
            "L,1,4,1,1\n" +
            "L,1,4,1,2\n" +
            "L,1,4,1,3\n" +
            "B,1,4,1,4\n" +
            "R,1,4,1,5\n" +
            "L,1,4,2,1\n" +
            "B,1,4,2,2\n" +
            "R,1,4,2,3\n" +
            "R,1,4,2,4\n" +
            "R,1,4,2,5\n" +
            "L,1,4,3,1\n" +
            "R,1,4,3,2\n" +
            "R,1,4,3,3\n" +
            "R,1,4,3,4\n" +
            "R,1,4,3,5\n" +
            "B,1,4,4,1\n" +
            "R,1,4,4,2\n" +
            "R,1,4,4,3\n" +
            "R,1,4,4,4\n" +
            "R,1,4,4,5\n" +
            "R,1,4,5,1\n" +
            "R,1,4,5,2\n" +
            "R,1,4,5,3\n" +
            "R,1,4,5,4\n" +
            "R,1,4,5,5\n" +
            "L,1,5,1,1\n" +
            "L,1,5,1,2\n" +
            "L,1,5,1,3\n" +
            "L,1,5,1,4\n" +
            "B,1,5,1,5\n" +
            "L,1,5,2,1\n" +
            "L,1,5,2,2\n" +
            "R,1,5,2,3\n" +
            "R,1,5,2,4\n" +
            "R,1,5,2,5\n" +
            "L,1,5,3,1\n" +
            "R,1,5,3,2\n" +
            "R,1,5,3,3\n" +
            "R,1,5,3,4\n" +
            "R,1,5,3,5\n" +
            "L,1,5,4,1\n" +
            "R,1,5,4,2\n" +
            "R,1,5,4,3\n" +
            "R,1,5,4,4\n" +
            "R,1,5,4,5\n" +
            "B,1,5,5,1\n" +
            "R,1,5,5,2\n" +
            "R,1,5,5,3\n" +
            "R,1,5,5,4\n" +
            "R,1,5,5,5\n" +
            "L,2,1,1,1\n" +
            "B,2,1,1,2\n" +
            "R,2,1,1,3\n" +
            "R,2,1,1,4\n" +
            "R,2,1,1,5\n" +
            "B,2,1,2,1\n" +
            "R,2,1,2,2\n" +
            "R,2,1,2,3\n" +
            "R,2,1,2,4\n" +
            "R,2,1,2,5\n" +
            "R,2,1,3,1\n" +
            "R,2,1,3,2\n" +
            "R,2,1,3,3\n" +
            "R,2,1,3,4\n" +
            "R,2,1,3,5\n" +
            "R,2,1,4,1\n" +
            "R,2,1,4,2\n" +
            "R,2,1,4,3\n" +
            "R,2,1,4,4\n" +
            "R,2,1,4,5\n" +
            "R,2,1,5,1\n" +
            "R,2,1,5,2\n" +
            "R,2,1,5,3\n" +
            "R,2,1,5,4\n" +
            "R,2,1,5,5\n" +
            "L,2,2,1,1\n" +
            "L,2,2,1,2\n" +
            "L,2,2,1,3\n" +
            "B,2,2,1,4\n" +
            "R,2,2,1,5\n" +
            "L,2,2,2,1\n" +
            "B,2,2,2,2\n" +
            "R,2,2,2,3\n" +
            "R,2,2,2,4\n" +
            "R,2,2,2,5\n" +
            "L,2,2,3,1\n" +
            "R,2,2,3,2\n" +
            "R,2,2,3,3\n" +
            "R,2,2,3,4\n" +
            "R,2,2,3,5\n" +
            "B,2,2,4,1\n" +
            "R,2,2,4,2\n" +
            "R,2,2,4,3\n" +
            "R,2,2,4,4\n" +
            "R,2,2,4,5\n" +
            "R,2,2,5,1\n" +
            "R,2,2,5,2\n" +
            "R,2,2,5,3\n" +
            "R,2,2,5,4\n" +
            "R,2,2,5,5\n" +
            "L,2,3,1,1\n" +
            "L,2,3,1,2\n" +
            "L,2,3,1,3\n" +
            "L,2,3,1,4\n" +
            "L,2,3,1,5\n" +
            "L,2,3,2,1\n" +
            "L,2,3,2,2\n" +
            "B,2,3,2,3\n" +
            "R,2,3,2,4\n" +
            "R,2,3,2,5\n" +
            "L,2,3,3,1\n" +
            "B,2,3,3,2\n" +
            "R,2,3,3,3\n" +
            "R,2,3,3,4\n" +
            "R,2,3,3,5\n" +
            "L,2,3,4,1\n" +
            "R,2,3,4,2\n" +
            "R,2,3,4,3\n" +
            "R,2,3,4,4\n" +
            "R,2,3,4,5\n" +
            "L,2,3,5,1\n" +
            "R,2,3,5,2\n" +
            "R,2,3,5,3\n" +
            "R,2,3,5,4\n" +
            "R,2,3,5,5\n" +
            "L,2,4,1,1\n" +
            "L,2,4,1,2\n" +
            "L,2,4,1,3\n" +
            "L,2,4,1,4\n" +
            "L,2,4,1,5\n" +
            "L,2,4,2,1\n" +
            "L,2,4,2,2\n" +
            "L,2,4,2,3\n" +
            "B,2,4,2,4\n" +
            "R,2,4,2,5\n" +
            "L,2,4,3,1\n" +
            "L,2,4,3,2\n" +
            "R,2,4,3,3\n" +
            "R,2,4,3,4\n" +
            "R,2,4,3,5\n" +
            "L,2,4,4,1\n" +
            "B,2,4,4,2\n" +
            "R,2,4,4,3\n" +
            "R,2,4,4,4\n" +
            "R,2,4,4,5\n" +
            "L,2,4,5,1\n" +
            "R,2,4,5,2\n" +
            "R,2,4,5,3\n" +
            "R,2,4,5,4\n" +
            "R,2,4,5,5\n" +
            "L,2,5,1,1\n" +
            "L,2,5,1,2\n" +
            "L,2,5,1,3\n" +
            "L,2,5,1,4\n" +
            "L,2,5,1,5\n" +
            "L,2,5,2,1\n" +
            "L,2,5,2,2\n" +
            "L,2,5,2,3\n" +
            "L,2,5,2,4\n" +
            "B,2,5,2,5\n" +
            "L,2,5,3,1\n" +
            "L,2,5,3,2\n" +
            "L,2,5,3,3\n" +
            "R,2,5,3,4\n" +
            "R,2,5,3,5\n" +
            "L,2,5,4,1\n" +
            "L,2,5,4,2\n" +
            "R,2,5,4,3\n" +
            "R,2,5,4,4\n" +
            "R,2,5,4,5\n" +
            "L,2,5,5,1\n" +
            "B,2,5,5,2\n" +
            "R,2,5,5,3\n" +
            "R,2,5,5,4\n" +
            "R,2,5,5,5\n" +
            "L,3,1,1,1\n" +
            "L,3,1,1,2\n" +
            "B,3,1,1,3\n" +
            "R,3,1,1,4\n" +
            "R,3,1,1,5\n" +
            "L,3,1,2,1\n" +
            "R,3,1,2,2\n" +
            "R,3,1,2,3\n" +
            "R,3,1,2,4\n" +
            "R,3,1,2,5\n" +
            "B,3,1,3,1\n" +
            "R,3,1,3,2\n" +
            "R,3,1,3,3\n" +
            "R,3,1,3,4\n" +
            "R,3,1,3,5\n" +
            "R,3,1,4,1\n" +
            "R,3,1,4,2\n" +
            "R,3,1,4,3\n" +
            "R,3,1,4,4\n" +
            "R,3,1,4,5\n" +
            "R,3,1,5,1\n" +
            "R,3,1,5,2\n" +
            "R,3,1,5,3\n" +
            "R,3,1,5,4\n" +
            "R,3,1,5,5\n" +
            "L,3,2,1,1\n" +
            "L,3,2,1,2\n" +
            "L,3,2,1,3\n" +
            "L,3,2,1,4\n" +
            "L,3,2,1,5\n" +
            "L,3,2,2,1\n" +
            "L,3,2,2,2\n" +
            "B,3,2,2,3\n" +
            "R,3,2,2,4\n" +
            "R,3,2,2,5\n" +
            "L,3,2,3,1\n" +
            "B,3,2,3,2\n" +
            "R,3,2,3,3\n" +
            "R,3,2,3,4\n" +
            "R,3,2,3,5\n" +
            "L,3,2,4,1\n" +
            "R,3,2,4,2\n" +
            "R,3,2,4,3\n" +
            "R,3,2,4,4\n" +
            "R,3,2,4,5\n" +
            "L,3,2,5,1\n" +
            "R,3,2,5,2\n" +
            "R,3,2,5,3\n" +
            "R,3,2,5,4\n" +
            "R,3,2,5,5\n" +
            "L,3,3,1,1\n" +
            "L,3,3,1,2\n" +
            "L,3,3,1,3\n" +
            "L,3,3,1,4\n" +
            "L,3,3,1,5\n" +
            "L,3,3,2,1\n" +
            "L,3,3,2,2\n" +
            "L,3,3,2,3\n" +
            "L,3,3,2,4\n" +
            "R,3,3,2,5\n" +
            "L,3,3,3,1\n" +
            "L,3,3,3,2\n" +
            "B,3,3,3,3\n" +
            "R,3,3,3,4\n" +
            "R,3,3,3,5\n" +
            "L,3,3,4,1\n" +
            "L,3,3,4,2\n" +
            "R,3,3,4,3\n" +
            "R,3,3,4,4\n" +
            "R,3,3,4,5\n" +
            "L,3,3,5,1\n" +
            "R,3,3,5,2\n" +
            "R,3,3,5,3\n" +
            "R,3,3,5,4\n" +
            "R,3,3,5,5\n" +
            "L,3,4,1,1\n" +
            "L,3,4,1,2\n" +
            "L,3,4,1,3\n" +
            "L,3,4,1,4\n" +
            "L,3,4,1,5\n" +
            "L,3,4,2,1\n" +
            "L,3,4,2,2\n" +
            "L,3,4,2,3\n" +
            "L,3,4,2,4\n" +
            "L,3,4,2,5\n" +
            "L,3,4,3,1\n" +
            "L,3,4,3,2\n" +
            "L,3,4,3,3\n" +
            "B,3,4,3,4\n" +
            "R,3,4,3,5\n" +
            "L,3,4,4,1\n" +
            "L,3,4,4,2\n" +
            "B,3,4,4,3\n" +
            "R,3,4,4,4\n" +
            "R,3,4,4,5\n" +
            "L,3,4,5,1\n" +
            "L,3,4,5,2\n" +
            "R,3,4,5,3\n" +
            "R,3,4,5,4\n" +
            "R,3,4,5,5\n" +
            "L,3,5,1,1\n" +
            "L,3,5,1,2\n" +
            "L,3,5,1,3\n" +
            "L,3,5,1,4\n" +
            "L,3,5,1,5\n" +
            "L,3,5,2,1\n" +
            "L,3,5,2,2\n" +
            "L,3,5,2,3\n" +
            "L,3,5,2,4\n" +
            "L,3,5,2,5\n" +
            "L,3,5,3,1\n" +
            "L,3,5,3,2\n" +
            "L,3,5,3,3\n" +
            "L,3,5,3,4\n" +
            "B,3,5,3,5\n" +
            "L,3,5,4,1\n" +
            "L,3,5,4,2\n" +
            "L,3,5,4,3\n" +
            "R,3,5,4,4\n" +
            "R,3,5,4,5\n" +
            "L,3,5,5,1\n" +
            "L,3,5,5,2\n" +
            "B,3,5,5,3\n" +
            "R,3,5,5,4\n" +
            "R,3,5,5,5\n" +
            "L,4,1,1,1\n" +
            "L,4,1,1,2\n" +
            "L,4,1,1,3\n" +
            "B,4,1,1,4\n" +
            "R,4,1,1,5\n" +
            "L,4,1,2,1\n" +
            "B,4,1,2,2\n" +
            "R,4,1,2,3\n" +
            "R,4,1,2,4\n" +
            "R,4,1,2,5\n" +
            "L,4,1,3,1\n" +
            "R,4,1,3,2\n" +
            "R,4,1,3,3\n" +
            "R,4,1,3,4\n" +
            "R,4,1,3,5\n" +
            "B,4,1,4,1\n" +
            "R,4,1,4,2\n" +
            "R,4,1,4,3\n" +
            "R,4,1,4,4\n" +
            "R,4,1,4,5\n" +
            "R,4,1,5,1\n" +
            "R,4,1,5,2\n" +
            "R,4,1,5,3\n" +
            "R,4,1,5,4\n" +
            "R,4,1,5,5\n" +
            "L,4,2,1,1\n" +
            "L,4,2,1,2\n" +
            "L,4,2,1,3\n" +
            "L,4,2,1,4\n" +
            "L,4,2,1,5\n" +
            "L,4,2,2,1\n" +
            "L,4,2,2,2\n" +
            "L,4,2,2,3\n" +
            "B,4,2,2,4\n" +
            "R,4,2,2,5\n" +
            "L,4,2,3,1\n" +
            "L,4,2,3,2\n" +
            "R,4,2,3,3\n" +
            "R,4,2,3,4\n" +
            "R,4,2,3,5\n" +
            "L,4,2,4,1\n" +
            "B,4,2,4,2\n" +
            "R,4,2,4,3\n" +
            "R,4,2,4,4\n" +
            "R,4,2,4,5\n" +
            "L,4,2,5,1\n" +
            "R,4,2,5,2\n" +
            "R,4,2,5,3\n" +
            "R,4,2,5,4\n" +
            "R,4,2,5,5\n" +
            "L,4,3,1,1\n" +
            "L,4,3,1,2\n" +
            "L,4,3,1,3\n" +
            "L,4,3,1,4\n" +
            "L,4,3,1,5\n" +
            "L,4,3,2,1\n" +
            "L,4,3,2,2\n" +
            "L,4,3,2,3\n" +
            "L,4,3,2,4\n" +
            "L,4,3,2,5\n" +
            "L,4,3,3,1\n" +
            "L,4,3,3,2\n" +
            "L,4,3,3,3\n" +
            "B,4,3,3,4\n" +
            "R,4,3,3,5\n" +
            "L,4,3,4,1\n" +
            "L,4,3,4,2\n" +
            "B,4,3,4,3\n" +
            "R,4,3,4,4\n" +
            "R,4,3,4,5\n" +
            "L,4,3,5,1\n" +
            "L,4,3,5,2\n" +
            "R,4,3,5,3\n" +
            "R,4,3,5,4\n" +
            "R,4,3,5,5\n" +
            "L,4,4,1,1\n" +
            "L,4,4,1,2\n" +
            "L,4,4,1,3\n" +
            "L,4,4,1,4\n" +
            "L,4,4,1,5\n" +
            "L,4,4,2,1\n" +
            "L,4,4,2,2\n" +
            "L,4,4,2,3\n" +
            "L,4,4,2,4\n" +
            "L,4,4,2,5\n" +
            "L,4,4,3,1\n" +
            "L,4,4,3,2\n" +
            "L,4,4,3,3\n" +
            "L,4,4,3,4\n" +
            "L,4,4,3,5\n" +
            "L,4,4,4,1\n" +
            "L,4,4,4,2\n" +
            "L,4,4,4,3\n" +
            "B,4,4,4,4\n" +
            "R,4,4,4,5\n" +
            "L,4,4,5,1\n" +
            "L,4,4,5,2\n" +
            "L,4,4,5,3\n" +
            "R,4,4,5,4\n" +
            "R,4,4,5,5\n" +
            "L,4,5,1,1\n" +
            "L,4,5,1,2\n" +
            "L,4,5,1,3\n" +
            "L,4,5,1,4\n" +
            "L,4,5,1,5\n" +
            "L,4,5,2,1\n" +
            "L,4,5,2,2\n" +
            "L,4,5,2,3\n" +
            "L,4,5,2,4\n" +
            "L,4,5,2,5\n" +
            "L,4,5,3,1\n" +
            "L,4,5,3,2\n" +
            "L,4,5,3,3\n" +
            "L,4,5,3,4\n" +
            "L,4,5,3,5\n" +
            "L,4,5,4,1\n" +
            "L,4,5,4,2\n" +
            "L,4,5,4,3\n" +
            "L,4,5,4,4\n" +
            "B,4,5,4,5\n" +
            "L,4,5,5,1\n" +
            "L,4,5,5,2\n" +
            "L,4,5,5,3\n" +
            "B,4,5,5,4\n" +
            "R,4,5,5,5\n" +
            "L,5,1,1,1\n" +
            "L,5,1,1,2\n" +
            "L,5,1,1,3\n" +
            "L,5,1,1,4\n" +
            "B,5,1,1,5\n" +
            "L,5,1,2,1\n" +
            "L,5,1,2,2\n" +
            "R,5,1,2,3\n" +
            "R,5,1,2,4\n" +
            "R,5,1,2,5\n" +
            "L,5,1,3,1\n" +
            "R,5,1,3,2\n" +
            "R,5,1,3,3\n" +
            "R,5,1,3,4\n" +
            "R,5,1,3,5\n" +
            "L,5,1,4,1\n" +
            "R,5,1,4,2\n" +
            "R,5,1,4,3\n" +
            "R,5,1,4,4\n" +
            "R,5,1,4,5\n" +
            "B,5,1,5,1\n" +
            "R,5,1,5,2\n" +
            "R,5,1,5,3\n" +
            "R,5,1,5,4\n" +
            "R,5,1,5,5\n" +
            "L,5,2,1,1\n" +
            "L,5,2,1,2\n" +
            "L,5,2,1,3\n" +
            "L,5,2,1,4\n" +
            "L,5,2,1,5\n" +
            "L,5,2,2,1\n" +
            "L,5,2,2,2\n" +
            "L,5,2,2,3\n" +
            "L,5,2,2,4\n" +
            "B,5,2,2,5\n" +
            "L,5,2,3,1\n" +
            "L,5,2,3,2\n" +
            "L,5,2,3,3\n" +
            "R,5,2,3,4\n" +
            "R,5,2,3,5\n" +
            "L,5,2,4,1\n" +
            "L,5,2,4,2\n" +
            "R,5,2,4,3\n" +
            "R,5,2,4,4\n" +
            "R,5,2,4,5\n" +
            "L,5,2,5,1\n" +
            "B,5,2,5,2\n" +
            "R,5,2,5,3\n" +
            "R,5,2,5,4\n" +
            "R,5,2,5,5\n" +
            "L,5,3,1,1\n" +
            "L,5,3,1,2\n" +
            "L,5,3,1,3\n" +
            "L,5,3,1,4\n" +
            "L,5,3,1,5\n" +
            "L,5,3,2,1\n" +
            "L,5,3,2,2\n" +
            "L,5,3,2,3\n" +
            "L,5,3,2,4\n" +
            "L,5,3,2,5\n" +
            "L,5,3,3,1\n" +
            "L,5,3,3,2\n" +
            "L,5,3,3,3\n" +
            "L,5,3,3,4\n" +
            "B,5,3,3,5\n" +
            "L,5,3,4,1\n" +
            "L,5,3,4,2\n" +
            "L,5,3,4,3\n" +
            "R,5,3,4,4\n" +
            "R,5,3,4,5\n" +
            "L,5,3,5,1\n" +
            "L,5,3,5,2\n" +
            "B,5,3,5,3\n" +
            "R,5,3,5,4\n" +
            "R,5,3,5,5\n" +
            "L,5,4,1,1\n" +
            "L,5,4,1,2\n" +
            "L,5,4,1,3\n" +
            "L,5,4,1,4\n" +
            "L,5,4,1,5\n" +
            "L,5,4,2,1\n" +
            "L,5,4,2,2\n" +
            "L,5,4,2,3\n" +
            "L,5,4,2,4\n" +
            "L,5,4,2,5\n" +
            "L,5,4,3,1\n" +
            "L,5,4,3,2\n" +
            "L,5,4,3,3\n" +
            "L,5,4,3,4\n" +
            "L,5,4,3,5\n" +
            "L,5,4,4,1\n" +
            "L,5,4,4,2\n" +
            "L,5,4,4,3\n" +
            "L,5,4,4,4\n" +
            "B,5,4,4,5\n" +
            "L,5,4,5,1\n" +
            "L,5,4,5,2\n" +
            "L,5,4,5,3\n" +
            "B,5,4,5,4\n" +
            "R,5,4,5,5\n" +
            "L,5,5,1,1\n" +
            "L,5,5,1,2\n" +
            "L,5,5,1,3\n" +
            "L,5,5,1,4\n" +
            "L,5,5,1,5\n" +
            "L,5,5,2,1\n" +
            "L,5,5,2,2\n" +
            "L,5,5,2,3\n" +
            "L,5,5,2,4\n" +
            "L,5,5,2,5\n" +
            "L,5,5,3,1\n" +
            "L,5,5,3,2\n" +
            "L,5,5,3,3\n" +
            "L,5,5,3,4\n" +
            "L,5,5,3,5\n" +
            "L,5,5,4,1\n" +
            "L,5,5,4,2\n" +
            "L,5,5,4,3\n" +
            "L,5,5,4,4\n" +
            "L,5,5,4,5\n" +
            "L,5,5,5,1\n" +
            "L,5,5,5,2\n" +
            "L,5,5,5,3\n" +
            "L,5,5,5,4\n" +
            "B,5,5,5,5\n";
}