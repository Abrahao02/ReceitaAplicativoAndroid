package com.example.receitaaplicativo;

import java.io.Serializable;
import java.util.ArrayList;

public class Receita implements Serializable {
    private String nomeReceita;
    private ArrayList<Ingrediente> ingredientes = new ArrayList<>();

    public Receita(String nomeReceita) {
        this.nomeReceita = nomeReceita;
    }

    public void adicionarIngrediente(Ingrediente ingrediente) {
        ingredientes.add(ingrediente);
    }

    public ArrayList<Ingrediente> getIngredientes() {
        return ingredientes;
    }
    public double calcularCustoTotal() {
        double custoTotal = 0;
        for (Ingrediente ingrediente : ingredientes) {
            custoTotal += ingrediente.calcularCusto();
        }
        return custoTotal;
    }

    public String getNomeReceita() {
        return nomeReceita;
    }

    public void setNomeReceita(String nomeReceita) {
        this.nomeReceita = nomeReceita;
    }
}

class Ingrediente implements Serializable {
    private String nome;
    private double preco;
    private double quantidade;
    private double quantidadeUtilizada;



    public Ingrediente() {
        // Construtor padrão vazio necessário para desserialização pelo Firebase
    }

    public void editarIngrediente(String novoNome, double novoPreco, double novaQuantidade) {
        this.nome = novoNome;
        this.preco = novoPreco;
        this.quantidade = novaQuantidade;
    }

    public double calcularCusto() {
        double custoUnitario = preco / quantidade;
        return custoUnitario * quantidadeUtilizada;
    }

    public String getNomeIngrediente() {
        return nome;
    }

    public void setNomeIngrediente(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public double getQuantidadeUtilizada() {
        return quantidadeUtilizada;
    }
    public void setQuantidadeUtilizada(double quantidadeUtilizada) {
        this.quantidadeUtilizada = quantidadeUtilizada;
    }
}
