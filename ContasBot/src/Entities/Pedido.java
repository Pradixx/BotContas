package Entities;

import java.time.LocalDate;

public class Pedido {
    private int id;
    private String produto;
    private int quantidade;
    private double preco;
    private LocalDate data;
    private int clienteId;

    public Pedido(String produto, int quantidade, double preco, LocalDate data, int clienteId) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = preco;
        this.data = data;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public LocalDate getData() {
        return data;
    }

    public int getClienteId() {
        return clienteId;
    }

}
