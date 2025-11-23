package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Espaco {
    private String id;
    private String nome;
    private TipoEspaco tipo;
    private double valorHora;
    private String localizacao;
    private int capacidade;
    private boolean disponivel;
    private boolean exclusivoVIP;

    public Espaco(String id, String nome, TipoEspaco tipo, double valorHora,
                  String localizacao, int capacidade, boolean exclusivoVIP) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.valorHora = valorHora;
        this.localizacao = localizacao;
        this.capacidade = capacidade;
        this.disponivel = true;
        this.exclusivoVIP = exclusivoVIP;
    }

    public String cadastrarEspaco() {
        try {
            Path caminho = Path.of("espacos/espacos.txt");
            Files.createDirectories(caminho.getParent());

            String conteudo = String.format("%s|%s|%s|%.2f|%s|%d|%s|%s%n",
                    id, nome, tipo.name(), valorHora, localizacao, capacidade, disponivel, exclusivoVIP);

            Files.writeString(caminho, conteudo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return "✅ Espaço cadastrado com sucesso!";
        } catch (IOException e) {
            return "❌ Erro ao cadastrar espaço: " + e.getMessage();
        }
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoEspaco getTipo() { return tipo; }
    public double getValorHora() { return valorHora; }
    public String getLocalizacao() { return localizacao; }
    public int getCapacidade() { return capacidade; }
    public boolean isDisponivel() { return disponivel; }
    public boolean isExclusivoVIP() { return exclusivoVIP; }

    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
