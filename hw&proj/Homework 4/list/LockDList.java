package list;

/**
 * User: ted
 * Date: 18-10-8 下午3:16
 */


public class LockDList extends DList{

    LockDList(){
        super();
    }

    protected DListNode newNode(Object item, DListNode prev, DListNode next) { return new LockDListNode(item, prev, next); }

    public void lockNode(DListNode node) {
        LockDListNode n = (LockDListNode) newNode(node.item, node.prev, node.next);
        DListNode pre = node.prev;
        DListNode nex = node.next;
        pre.next = n;
        nex.prev = n;
    }

    public void remove(LockDListNode node) {
        // Your solution here.
        if (node.isLocked()) {
            ;
        } else {
            super.remove(node);
        }

    }
}