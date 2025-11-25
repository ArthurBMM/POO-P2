package org.example;

import java.util.List;

public class Relatorios {
    public void gerarRelatorioOcupacao(SistemaReservas reservas) {
        System.out.println("📊 RELATÓRIO DE OCUPAÇÃO");
        System.out.println("═══════════════════════════════════════");

        List<Espaco> todosEspacos = reservas.getTodosEspacos();
        long totalEspacos = todosEspacos.size();
        long espacosOcupados = todosEspacos.stream().filter(e -> !e.isDisponivel()).count();
        double taxaOcupacao = (double) espacosOcupados / totalEspacos * 100;

        System.out.printf("Espaços totais: %d%n", totalEspacos);
        System.out.printf("Espaços ocupados: %d%n", espacosOcupados);
        System.out.printf("Taxa de ocupação: %.1f%%%n", taxaOcupacao);
        System.out.println();

        System.out.println("📈 DETALHAMENTO POR ESPAÇO:");
        for (Espaco espaco : todosEspacos) {
            String status = espaco.isDisponivel() ? "🟢 Disponível" : "🔴 Ocupado";
            System.out.printf("• %s: %s | Capacidade: %d | Valor/h: R$ %.2f%n",
                    espaco.getNome(), status, espaco.getCapacidade(),
                    espaco.getValorHora());
        }
    }

    public void gerarRelatorioFinanceiro(SistemaPagamentos pagamentos, Pessoa usuario) {
        System.out.println("💵 RELATÓRIO FINANCEIRO");
        System.out.println("═══════════════════════════════════════");

        System.out.printf("Saldo atual: R$ %.2f%n", usuario.getSaldo());
        System.out.printf("Total de aluguéis: %d%n", usuario.getHistoricoAlugueis().size());
        System.out.printf("Total de pagamentos: %d%n", usuario.getHistoricoPagamentos().size());

        double totalGasto = usuario.getHistoricoAlugueis().stream()
                .mapToDouble(hist -> {
                    // Extrair valor do histórico
                    try {
                        // Extrair valor do histórico de forma mais robusta
                        String[] partes = hist.split("R\\$ ");
                        if (partes.length > 1) {
                            String valorStr = partes[1].split(" ")[0].trim();
                            // Substituir vírgula por ponto para conversão
                            valorStr = valorStr.replace(",", ".");
                            return Double.parseDouble(valorStr);
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao processar histórico: " + hist);
                    }
                    return 0.0;
                })
                .sum();

        System.out.printf("Total gasto em aluguéis: R$ %.2f%n", totalGasto);
    }
}
