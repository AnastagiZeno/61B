package list;

/**
 * User: ted
 * Date: 18-10-8 下午3:11
 */


public class LockDListNode extends DListNode {

    private Boolean locked;

    LockDListNode(Object i, DListNode p, DListNode n) {
        super(i, p, n);
        locked = false;
    }

    public void lock(){ locked = true; }
    public Boolean isLocked() { return locked; }

}
