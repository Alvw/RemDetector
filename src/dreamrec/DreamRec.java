package dreamrec;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        final LinkedBlockingQueue<Integer> buffer = new LinkedBlockingQueue<Integer>(4);
        Controller controller = new Controller();
    }
}
