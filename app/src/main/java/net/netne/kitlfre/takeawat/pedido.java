package net.netne.kitlfre.takeawat;

/**
 * Created by Bruno Martins
 */
public class pedido {
    private Produto p = null;
    private Double quant = 0.0;

    public pedido(Produto p, Double quant) {
        this.p = p;
        this.quant = quant;
    }

    public Produto getP() {
        return p;
    }

    public void setP(Produto p) {
        this.p = p;
    }

    public Double getQuant() {
        return quant;
    }

    public void setQuant(Double quant) {
        this.quant = quant;
    }
}
