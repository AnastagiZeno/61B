package list;

/**
 * User: ted
 * Date: 18-10-8 下午3:03
 */


public class Test {
    public static void main(String[] args){
        DList l = new DList();
        l.insertBack(1);
        l.insertBack(2);
        l.insertFront(8);
        l.insertBack(3);
        l.insertBack(4);
        l.insertBack(5);
        l.insertFront(9);
        System.out.println(l);
    }
}
