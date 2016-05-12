package net.netne.kitlfre.takeawat;

/**
 * Created by Bruno Martins
 */

public class User {
    String email, password, nome, telefone, morada;

    User(String email,String password)
    {
        this.email=email;
        this.password=password;
    }

    User(String email, String password, String nome, String telefone, String morada)
    {
        this.email=email;
        this.password=password;
        this.nome=nome;
        this.telefone=telefone;
        this.email=email;
        this.morada=morada;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }
}
