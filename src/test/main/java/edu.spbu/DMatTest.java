package edu.spbu;
import static org.junit.Assert.assertEquals;

public class DMatTest {
    @org.junit.Test
    public void mulDD() throws Exception {
        Matrix m1 = new DMat("1.txt");
        Matrix m2 = new DMat("1.txt");
        Matrix goldenResult = m1.mul(m2);
        Matrix m =  m1.pmul(m2);
        assertEquals(m,goldenResult);
    }

}
