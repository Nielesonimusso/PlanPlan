package nl.group11.planplan;

import junit.framework.TestCase;

/**
 * Created by s132316 on 29-3-2016.
 */
public class NumberAddertest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testNumberAdder (){

        int result = NumberAdder.addNumbers(2,3);
        assertEquals(result, 5);
    }

    protected void tearDown() throws Exception {
        super.tearDown();

    }
}
