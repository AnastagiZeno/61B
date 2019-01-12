package player;

import java.util.ArrayList;

/**
 * Author: bingxing.k
 * Date: 18-11-22 上午11:30
 */


public class Connection {
    private Chip item;
    private Connection father;
    private ArrayList<Connection> children;

    public void setItem(Chip item) {
        this.item = item;
    }

    public Connection(Chip item) {
        this.item = item;
    }


    public void setFather(Connection father) {
        this.father = father;
    }

    void addChild(Connection child) {
        child.setFather(this);
        if (null == children) {
            this.children = new ArrayList<>();
        }
        children.add(child);
    }

    public ArrayList<Chip> connectionList(){
        ArrayList<Chip> l = new ArrayList<>();
        // add the leaf item first
        l.add(item);
        // recursively add father's item
        Connection current_father = father;
        while (null != current_father) {
            l.add(current_father.item);
            current_father = current_father.father;
        }
        return l;
    }

    public ArrayList<ArrayList<Chip>> traverse() {
        ArrayList<ArrayList<Chip>> l = new ArrayList<>();
        if (null == children) {
            l.add(connectionList());
        }
        else {
            for (Connection conn: children) {
                l.addAll(conn.traverse());
            }
        }
        return l;
    }

    @Override
    public String toString() {
        ArrayList<ArrayList<Chip>> result = traverse();
        String s = String.format("Total %d Connections:\n", result.size());
        for (int i = 0; i< result.size(); i++) {
            String line = String.format("[%d] ", i);
            for (int j=result.get(i).size()-1; j>=0; j--) {
                Chip c = result.get(i).get(j);
                line += c.toString();
                if (j != 0) {
                    line += " --> ";
                } else {
                    line += " $";
                }
            }
            s = s + line + "\n";
        }
        return s;
    }
}
