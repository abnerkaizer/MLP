import java.util.List;
import java.util.Random;
public class MLP {
    private double [][] wh;
    private double [][] wo;

    private int qtdIn,qtdH,qtdOut;
    private double ni;
    private Random rand;
    public MLP(int qtdIn,int qtdH,int qtdOut,double ni){
        rand = new Random();
        this.qtdIn = qtdIn;
        this.qtdH = qtdH;
        this.qtdOut = qtdOut;
        this.ni = ni;
        this.wh = new double[qtdIn+1][qtdH];
        this.wo = new double[qtdH+1][qtdOut];
        for (int i = 0; i < wh.length; i++) {
            for (int j = 0; j < wh[0].length; j++) {
                wh[i][j] = -0.03 + 0.06 * rand.nextDouble();// [-0.03,0.03]
            }
        }
        for (int i = 0; i < wo.length; i++) {
            for (int j = 0; j < wo[0].length; j++) {
                wo[i][j] = -0.03 + 0.06 * rand.nextDouble();// [-0.03,0.03]
            }
        }
    }
    public double[] treinar(List<Double> x, double[] y) {
        double H[] = new double [qtdH+1];
        for (int j = 0; j < H.length-1; j++) {
            double u = 0;
            int i=0;
            for (double n:x) {
                u += n * this.wh[i][j];
                i++;
            }
            H[j] = 1 / (1 + Math.exp(-u));
        }
        H[H.length-1] = 1;
        double out[] = new double[qtdOut];
        for (int j = 0; j <out.length; j++) {
            double u=0;
            for (int i = 0; i < H.length; i++) {
                u += H[i]*wo[i][j];
            }
            out[j] =  1 / (1 + Math.exp(-u));
        }
        double[] deltaO = new double[qtdOut];
        for (int j = 0; j < deltaO.length; j++) {
            if (out[j]>y[j]){
                deltaO[j] = out[j] * (1-out[j])*Math.pow(y[j]-out[j],2)*-1;
            }else
                deltaO[j] = out[j] * (1-out[j])*Math.pow(y[j]-out[j],2);
        }
        double [] deltaH = new double[qtdH];
        for (int h = 0; h < deltaH.length; h++) {
            double s = 0;
            for (int j = 0; j < out.length; j++) {
                s+=deltaO[j]*wo[h][j];
            }
            deltaH[h] = H[h]*(1-H[h])*s;
        }
        for (int i = 0; i < wh.length; i++) {
            for (int h = 0; h < wh[0].length; h++) {
                wh[i][h]+= ni*deltaH[h]*x.get(i);
            }
        }
        for (int i = 0; i < wo.length; i++) {
            for (int h = 0; h < wo[0].length; h++) {
                wo[i][h]+= ni*deltaO[h]*H[i];
            }
        }
        return out;
    }
    public double[] teste(List<Double> x, double[] y){
        double H[] = new double [qtdH+1];
        for (int j = 0; j < H.length-1; j++) {
            double u = 0;
            int i=0;
            for (double n:x) {
                u += n * this.wh[i][j];
                i++;
            }
            H[j] = 1 / (1 + Math.exp(-u));
        }
        H[H.length-1] = 1;
        double out[] = new double[qtdOut];
        for (int j = 0; j <out.length; j++) {
            double u=0;
            for (int i = 0; i < H.length; i++) {
                u += H[i]*wo[i][j];
            }
            out[j] =  1 / (1 + Math.exp(-u));
        }
        return out;
    }
    private double[] executar(List<Double> x){
        double H[] = new double [qtdH+1];
        for (int j = 0; j < H.length-1; j++) {
            double u = 0;
            int i=0;
            for (double n:x) {
                u += n * this.wh[i][j];
                i++;
            }
            H[j] = 1 / (1 + Math.exp(-u));
        }
        H[H.length-1] = 1;
        double out[] = new double[qtdOut];
        for (int j = 0; j <out.length; j++) {
            double u=0;
            for (int i = 0; i < H.length; i++) {
                u += H[i]*wo[i][j];
            }
            out[j] =  1 / (1 + Math.exp(-u));
        }
        return out;
    }
}
