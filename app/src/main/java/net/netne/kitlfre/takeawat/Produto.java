package net.netne.kitlfre.takeawat;

import java.util.Date;

/**
 * Created by Bruno Martins
 */

public class Produto {
    private String designacao, preco = "";

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    private String dia;
    private Integer cod_produto = 0;

    public void setCod_produto(Integer cod){
        cod_produto = cod;
    }

    public void setDesignacao(String desi){
        designacao = desi;
    }

    public void setPreco(String prec){
        preco = prec;
    }

    public String getDesignacao() {
        return designacao;
    }

    public String getPreco() {
        return preco;
    }

    public Integer getCod_produto() {
        return cod_produto;
    }

    Produto(){

    }
    Produto(Integer cod_produto, String designacao, String preco, String dia)
    {
        this.cod_produto = cod_produto;
        this.designacao = designacao;
        this.preco = preco;
        this.dia = dia;
    }

    public String toString()
    {
        return( designacao + "(" + preco + " €)" );
    }
}
