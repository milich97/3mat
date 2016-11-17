package edu.spbu;

import static org.junit.Assert.assertEquals;

public class DMatTest {
    @org.junit.Test
    public void mulDD() throws Exception {
        Matrix m1 = new DMat("1.txt");
        Matrix m2 = new DMat("1.txt");
        Matrix goldenResult = m1.mul(m2);
        Matrix m = m1.pmul(m2);
        m.saveToFile("11.txt");
        assertEquals(m, goldenResult);
    }

    @org.junit.Test
    public void mulSS() throws Exception {
        Matrix m1 = new SMat("1.txt");
        Matrix m2 = new SMat("1.txt");
        Matrix goldenResult = m1.mul(m2);
        Matrix m = m1.pmul(m2);
        assertEquals(m, goldenResult);
    }

}
