package edu.spbu;//sparse - разреженный

import java.io.*;
import java.util.*;

public class SMat implements Matrix {

    public SMat(String fileName) throws IOException {
        if (fileName != null) {
            fillArrays(this, fileName);
        }
    }

    private double[] toArray1(ArrayList<Double> arrayList) {
        double[] res = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            res[i] = (double) arrayList.get(i);
        }

        return res;
    }

    private int[] toArray2(ArrayList<Integer> arrayList) {
        int[] res = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            res[i] = (int) arrayList.get(i);
        }

        return res;
    }


    @Override
    public Matrix mul(Matrix bb) throws IOException {
        SMat a = this;
        SMat res = null;
        if (bb instanceof SMat) {
            SMat b = (SMat) bb;
            res = a.mulSS(b);
        }

        return res;

    }

    @Override
    public Matrix pmul(Matrix b) throws IOException, InterruptedException {
        SMat a = this;
        SMat res;
        res = a.pmulSS((SMat) b);
        return res;
    }

    @Override
    public void saveToFile(String nameOfFile) throws IOException {
        SMat c = this;
        double nol = 0;
        PrintWriter printWriter = new PrintWriter(new FileWriter(nameOfFile));
        for (int i = 0; i < c.pointersArr.length - 1; i++) {
            ArrayList<Integer> k = new ArrayList();
            for (int ii = c.pointersArr[i]; ii < c.pointersArr[i + 1]; ii++) {
                k.add(ii);
            }
            if (k.size() != 0) {
                int elemOfMasK = 0;
                for (int j = 0; j < c.pointersArr.length - 1; j++) {

                    if (elemOfMasK == k.size()) {
                        for (int l = j; l < c.pointersArr.length - 1; l++) printWriter.print(nol + " ");


                        break;
                    }

                    if (c.colsArr[k.get(elemOfMasK)] == j) {

                        printWriter.print(c.valuesArr[k.get(elemOfMasK)] + " ");
                        elemOfMasK++;

                    } else printWriter.print(nol + " ");

                }

                printWriter.println();
            } else {
                for (int j = 0; j < c.pointersArr.length - 1; j++) printWriter.print(nol + " ");
                printWriter.println();
            }
        }
        printWriter.close();
    }

    public double[] valuesArr;
    public int[] colsArr;
    public int[] pointersArr;
    public int sizeOfMatrix;


    public static void main(String[] args) throws IOException, InterruptedException {

    }


    private SMat mulSS(SMat b) throws IOException {
        SMat a = this;
        SMat c = new SMat(null);
        b = b.transpose(b);
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        ArrayList<Integer> pointers = new ArrayList<>();
        c.sizeOfMatrix = a.sizeOfMatrix;
        double res;
        pointers.add(0);
        for (int stroka = 0; stroka < a.pointersArr.length - 1; stroka++) {
            for (int stolb = 0; stolb < b.pointersArr.length - 1; stolb++) {
                SMat v1 = new SMat(null);
                v1.sizeOfMatrix = a.sizeOfMatrix;
                v1.valuesArr = new double[a.pointersArr[stroka + 1] - a.pointersArr[stroka]];
                v1.colsArr = new int[a.pointersArr[stroka + 1] - a.pointersArr[stroka]];
                SMat v2 = new SMat(null);
                v2.sizeOfMatrix = b.sizeOfMatrix;
                v2.valuesArr = new double[b.pointersArr[stolb + 1] - b.pointersArr[stolb]];
                v2.colsArr = new int[b.pointersArr[stolb + 1] - b.pointersArr[stolb]];

                for (int i = a.pointersArr[stroka]; i < a.pointersArr[stroka + 1]; i++) {
                    v1.valuesArr[i - a.pointersArr[stroka]] = a.valuesArr[i];
                    v1.colsArr[i - a.pointersArr[stroka]] = a.colsArr[i];
                }

                for (int i = b.pointersArr[stolb]; i < b.pointersArr[stolb + 1]; i++) {
                    v2.valuesArr[i - b.pointersArr[stolb]] = b.valuesArr[i];
                    v2.colsArr[i - b.pointersArr[stolb]] = b.colsArr[i];
                }
                res = v1.scalarMul(v2);
                if (res != 0) {
                    values.add(res);
                    cols.add(stolb);
                }

            }
            pointers.add(values.size());
        }
        c.valuesArr = toArray1(values);
        c.colsArr = toArray2(cols);
        c.pointersArr = toArray2(pointers);
        return c;
    }

    private SMat pmulSS(SMat m2) throws InterruptedException, IOException {
        Dispatch d = new Dispatch();
        SMat m1 = this;
        SMat res = new SMat(null);
        m2 = m2.transpose(m2);
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        ArrayList<Integer> pointers = new ArrayList<>();
        double[][] resDMas = new double[m1.sizeOfMatrix][m1.sizeOfMatrix];
        res.sizeOfMatrix = m1.sizeOfMatrix;
        pointers.add(0);
        SMat finalM = m2;
        class MyCode implements Runnable {
            public void run() {
                for (int stroka = d.next(); stroka < m1.pointersArr.length - 1; stroka = d.next()) {
                    for (int stolb = 0; stolb < finalM.pointersArr.length - 1; stolb++) {
                        SMat v1 = null;
                        try {
                            v1 = new SMat(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        v1.sizeOfMatrix = m1.sizeOfMatrix;
                        v1.valuesArr = new double[m1.pointersArr[stroka + 1] - m1.pointersArr[stroka]];
                        v1.colsArr = new int[m1.pointersArr[stroka + 1] - m1.pointersArr[stroka]];
                        SMat v2 = null;
                        try {
                            v2 = new SMat(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        v2.sizeOfMatrix = finalM.sizeOfMatrix;
                        v2.valuesArr = new double[finalM.pointersArr[stolb + 1] - finalM.pointersArr[stolb]];
                        v2.colsArr = new int[finalM.pointersArr[stolb + 1] - finalM.pointersArr[stolb]];

                        for (int i = m1.pointersArr[stroka]; i < m1.pointersArr[stroka + 1]; i++) {
                            v1.valuesArr[i - m1.pointersArr[stroka]] = m1.valuesArr[i];
                            v1.colsArr[i - m1.pointersArr[stroka]] = m1.colsArr[i];
                        }

                        for (int i = finalM.pointersArr[stolb]; i < finalM.pointersArr[stolb + 1]; i++) {
                            v2.valuesArr[i - finalM.pointersArr[stolb]] = finalM.valuesArr[i];
                            v2.colsArr[i - finalM.pointersArr[stolb]] = finalM.colsArr[i];
                        }
                        //double resSc = v1.scalarMul(v2);
                        resDMas[stroka][stolb] = v1.scalarMul(v2);
                        //               ArrayList<Double> miniValues = new ArrayList<>();
                        //                 ArrayList<Integer> miniCols = new ArrayList<>();
                        //                   ArrayList<Integer> miniPointers = new ArrayList<>();
//                        if (resSc != 0)  {
//                            miniValues.add(resSc);
//                            miniCols.add(stolb);
//                            values.add(resSc);
//                            cols.add(stolb);
                        //                     }
                    }

                    //pointers.add(values.size());

                }
            }

        }
        Thread t1 = new Thread(new MyCode());
        Thread t2 = new Thread(new MyCode());
        Thread t3 = new Thread(new MyCode());
        Thread t4 = new Thread(new MyCode());
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();

//        res.valuesArr = toArray1(values);
//        res.colsArr = toArray2(cols);
//        res.pointersArr = toArray2(pointers);
        res = DToS(resDMas);

        return res;
    }

    public static void printf(SMat a) {
        printf(a.valuesArr);
        printf(a.colsArr);
        if (a.pointersArr != null) printf(a.pointersArr);
    }

    public static void printf(double[] d) {
        for (int i = 0; i < d.length; i++) {
            System.out.print(d[i] + "  ");
        }
        System.out.println();
    }

    public static void printf(int[] d) {
        for (int i = 0; i < d.length; i++) {
            System.out.print(d[i] + "  ");
        }
        System.out.println();
    }

    private double scalarMul(SMat b) {
        SMat a = this;
        int[] x = new int[a.sizeOfMatrix];
        for (int i = 0; i < x.length; i++) x[i] = -1;
        double s = 0;
        for (int i = 0; i < a.valuesArr.length; i++) {
            x[a.colsArr[i]] = i;
        }
        for (int i = 0; i < b.valuesArr.length; i++) {
            if (x[b.colsArr[i]] != -1) {
                s = s + b.valuesArr[i] * a.valuesArr[x[b.colsArr[i]]];
            }
        }
        return s;
    }

    public static SMat DToS(double[][] mas) throws IOException {
        SMat a = new SMat(null);

        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        ArrayList<Integer> pointers = new ArrayList<>();
        pointers.add(0);
        a.sizeOfMatrix = mas.length;
        for (int i = 0; i < mas.length; i++) {
            for (int j = 0; j < mas.length; j++) {

                if (mas[i][j] != 0) {
                    values.add(mas[i][j]);
                    cols.add(j);
                }
            }

            pointers.add(values.size());
        }
        a.valuesArr = a.toArray1(values);
        a.colsArr = a.toArray2(cols);
        a.pointersArr = a.toArray2(pointers);
        return a;
    }

    public static void fillArrays(SMat a, String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String s = reader.readLine();
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        ArrayList<Integer> pointers = new ArrayList<>();
        pointers.add(0);
        a.sizeOfMatrix = 0;
        while (s != null) {
            a.sizeOfMatrix++;
            int collNumber = 0;
            for (String val : s.split(" ")) {

                if (Double.parseDouble(val) != 0) {
                    values.add(Double.parseDouble(val));
                    cols.add(collNumber);
                }
                collNumber++;
            }


            s = reader.readLine();
            if (s != null) pointers.add(values.size());
        }
        pointers.add(values.size());
        a.valuesArr = a.toArray1(values);
        a.colsArr = a.toArray2(cols);
        a.pointersArr = a.toArray2(pointers);
    }

    public SMat transpose(SMat a) throws IOException {
        int j;
        double v;
        SMat newA = new SMat(null);
        newA.sizeOfMatrix = a.sizeOfMatrix;
        ArrayList intVectors[] = new ArrayList[a.pointersArr.length - 1];
        ArrayList doubleVectors[] = new ArrayList[a.pointersArr.length - 1];
        for (int i = 0; i < intVectors.length; i++) intVectors[i] = new ArrayList();
        for (int i = 0; i < doubleVectors.length; i++) doubleVectors[i] = new ArrayList();
        for (int i = 0; i < a.pointersArr.length - 1; i++) {
            for (int k = a.pointersArr[i]; k < a.pointersArr[i + 1]; k++) {
                j = a.colsArr[k];
                v = a.valuesArr[k];
                intVectors[j].add(i);
                doubleVectors[j].add(v);
            }
        }
        newA.valuesArr = new double[a.valuesArr.length];
        newA.colsArr = new int[a.colsArr.length];
        newA.pointersArr = new int[a.pointersArr.length];
        newA.pointersArr[0] = 0;
        for (int i = 1; i < a.pointersArr.length; i++)
            newA.pointersArr[i] = newA.pointersArr[i - 1] + intVectors[i - 1].size();
        int newK = -1;
        for (int i = 0; i < intVectors.length; i++) {
            for (int k = 0; k < intVectors[i].size(); k++) {
                newK++;
                newA.colsArr[newK] = (int) intVectors[i].get(k);
                newA.valuesArr[newK] = (double) doubleVectors[i].get(k);

            }
        }

        return newA;
    }


    @Override
    public boolean equals(Object bm) {
        boolean ans;
        if (bm instanceof SMat) {
            SMat b = (SMat) bm;
            SMat a = this;
            boolean ans1 = true;
            if (a.valuesArr.length == b.valuesArr.length && a.colsArr.length == b.colsArr.length && a.pointersArr.length == b.pointersArr.length) {
                for (int i = 0; i < a.valuesArr.length; i++) {
                    if (a.valuesArr[i] != b.valuesArr[i] || a.colsArr[i] != b.colsArr[i])
                        ans1 = false;


                }
                for (int i = 0; i < a.pointersArr.length; i++) {
                    if (a.pointersArr[i] != b.pointersArr[i]) ans1 = false;
                }
                if (a.sizeOfMatrix != b.sizeOfMatrix) ans1 = false;
            } else ans1 = false;
            ans = ans1;
        } else ans = false;
        return ans;
    }

}