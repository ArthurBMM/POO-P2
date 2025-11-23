package org.example;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Pessoa {
    protected String nome;
    protected String email;
    protected String senha;
    protected String assinatura;
    protected String cep;
    protected String cidade; // Nova campo para cidade
    protected Endereco endereco;
    protected List<String> historicoAlugueis;
    protected List<String> historicoPagamentos;
    protected double saldo;
    protected boolean ativo;
    protected LocalDateTime dataCadastro;

    public Pessoa() {
        this.historicoAlugueis = new ArrayList<>();
        this.historicoPagamentos = new ArrayList<>();
        this.saldo = 0.0;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    // Método para buscar endereço via CEP - ATUALIZADO
    public void buscarEnderecoPorCEP() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                this.endereco = gson.fromJson(response.body(), Endereco.class);
                if (this.endereco != null) {
                    this.cidade = this.endereco.getLocalidade(); // Define a cidade
                }
            } else {
                System.err.println("Erro na requisição: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar endereço: " + e.getMessage());
        }
    }

    // Salvar dados do usuário - ATUALIZADO
    public void salvarDadosUsuario() {
        try {
            String conteudo = String.join(System.lineSeparator(),
                    "Nome: " + this.nome,
                    "Email: " + this.email,
                    "Senha: " + this.senha,
                    "Assinatura: " + this.assinatura,
                    "CEP: " + (this.endereco != null ? this.endereco.getCep() : this.cep),
                    "Cidade: " + this.cidade, // Salva a cidade
                    "Logradouro: " + (this.endereco != null ? this.endereco.getLogradouro() : "Não encontrado"),
                    "Complemento: " + (this.endereco != null ? this.endereco.getComplemento() : "Não informado"),
                    "Bairro: " + (this.endereco != null ? this.endereco.getBairro() : "Não encontrado"),
                    "Localidade: " + (this.endereco != null ? this.endereco.getLocalidade() : "Não encontrado"),
                    "UF: " + (this.endereco != null ? this.endereco.getUf() : "Não encontrado"),
                    "DDD: " + (this.endereco != null ? this.endereco.getDdd() : "Não encontrado"),
                    "Saldo: " + this.saldo,
                    "DataCadastro: " + this.dataCadastro.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    System.lineSeparator());

            Path caminho = Path.of("usuarios/" + this.email + ".txt");
            Files.createDirectories(caminho.getParent());
            Files.writeString(caminho, conteudo, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("✅ Cadastro realizado com sucesso!");
            System.out.println("📍 Cidade detectada: " + this.cidade);
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar dados: " + e.getMessage());
        }
    }

    // Métodos abstratos
    public abstract double calcularDesconto(double valorOriginal);
    public abstract void mostrarBeneficios();
    public abstract boolean temAcessoExclusivo();

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getAssinatura() { return assinatura; }
    public void setAssinatura(String assinatura) { this.assinatura = assinatura; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getCidade() { return cidade; } // Novo getter
    public void setCidade(String cidade) { this.cidade = cidade; } // Novo setter
    public Endereco getEndereco() { return endereco; }
    public List<String> getHistoricoAlugueis() { return historicoAlugueis; }
    public List<String> getHistoricoPagamentos() { return historicoPagamentos; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
