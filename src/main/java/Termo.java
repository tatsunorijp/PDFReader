/**
 * Created by Tatsunori on 04/07/2018.
 */
public class Termo {

    private String name;
    private int qty;

    public Termo(String name, int qty) {
        this.name = name;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
