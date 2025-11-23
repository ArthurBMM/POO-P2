package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SistemaPagamentos {
    private List<String> transacoes;

    public SistemaPagamentos() {
        this.transacoes = new ArrayList<>();
    }

    public boolean processarPagamento(Pessoa usuario, double valor, String descricao) {
        if (valor <= 0) {
            System.out.println("❌ Valor deve ser positivo");
            return false;
        }

        usuario.setSaldo(usuario.getSaldo() + valor);

        String transacao = String.format("%s|%s|%.2f|%s|%s",
                usuario.getEmail(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                valor,
                descricao,
                "CONCLUIDO");

        transacoes.add(transacao);
        usuario.getHistoricoPagamentos().add(transacao);

        salvarTransacao(transacao);

        System.out.printf("✅ Pagamento processado: R$ %.2f | Novo saldo: R$ %.2f%n",
                valor, usuario.getSaldo());
        return true;
    }

    private void salvarTransacao(String transacao) {
        try {
            Path caminho = Path.of("pagamentos/transacoes.txt");
            Files.createDirectories(caminho.getParent());
            Files.writeString(caminho, transacao + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Erro ao salvar transação: " + e.getMessage());
        }
    }

    public void mostrarHistoricoPagamentos(Pessoa usuario) {
        System.out.println("💰 HISTÓRICO DE PAGAMENTOS");
        System.out.println("═══════════════════════════════════════");

        if (usuario.getHistoricoPagamentos().isEmpty()) {
            System.out.println("Nenhum pagamento realizado.");
            return;
        }

        for (int i = 0; i < usuario.getHistoricoPagamentos().size(); i++) {
            String[] partes = usuario.getHistoricoPagamentos().get(i).split("\\|");
            System.out.printf("%d. %s | R$ %s | %s%n",
                    i + 1, partes[1], partes[2], partes[3]);
        }
    }
}
