package org.example;

import java.time.LocalDateTime;
import java.util.*;
public class SistemaReservas {
    private List<Espaco> espacos;
    private List<Reserva> reservas;
    private Random random;

    public SistemaReservas() {
        this.espacos = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.random = new Random();
        carregarEspacosIniciais();
    }

    private void carregarEspacosIniciais() {
        // Espaços comuns
        espacos.add(new Espaco("SALA01", "Sala de Reuniões Pequena", TipoEspaco.SALA_REUNIAO, 80.0, "1º Andar", 6, false));
        espacos.add(new Espaco("SALA02", "Sala de Reuniões Média", TipoEspaco.SALA_REUNIAO, 120.0, "1º Andar", 10, false));
        espacos.add(new Espaco("SALA03", "Sala de Reuniões Grande", TipoEspaco.SALA_REUNIAO, 200.0, "2º Andar", 15, false));
        espacos.add(new Espaco("AUD01", "Auditório Principal", TipoEspaco.AUDITORIO, 500.0, "Térreo", 50, false));
        espacos.add(new Espaco("TREIN01", "Sala de Treinamento", TipoEspaco.SALA_TREINAMENTO, 150.0, "1º Andar", 20, false));
        espacos.add(new Espaco("COW01", "Área Coworking Aberta", TipoEspaco.AREA_COWORKING, 25.0, "Térreo", 30, false));

        // Espaços exclusivos VIP
        espacos.add(new Espaco("VIP01", "Sala VIP Executiva", TipoEspaco.SALA_VIP, 300.0, "3º Andar", 8, true));
        espacos.add(new Espaco("VIP02", "Sala de Reuniões Premium", TipoEspaco.SALA_REUNIAO, 400.0, "3º Andar", 12, true));
        espacos.add(new Espaco("VIP03", "Escritório Privativo VIP", TipoEspaco.ESCRITORIO_PRIVATIVO, 200.0, "3º Andar", 4, true));
    }

    public List<Espaco> getEspacosDisponiveis(Pessoa usuario) {
        return espacos.stream()
                .filter(espaco -> espaco.isDisponivel() &&
                        (!espaco.isExclusivoVIP() || usuario.temAcessoExclusivo()))
                .toList();
    }

    public List<Espaco> getTodosEspacos() {
        return espacos;
    }

    public boolean reservarEspaco(String idEspaco, Pessoa usuario, LocalDateTime dataInicio, int horas) {
        Optional<Espaco> espacoOpt = espacos.stream()
                .filter(e -> e.getId().equals(idEspaco) && e.isDisponivel())
                .findFirst();

        if (espacoOpt.isPresent()) {
            Espaco espaco = espacoOpt.get();

            // Verificar se é exclusivo VIP
            if (espaco.isExclusivoVIP() && !usuario.temAcessoExclusivo()) {
                System.out.println("❌ Acesso negado: Este espaço é exclusivo para clientes VIP");
                return false;
            }

            // Verificar conflito de horários
            if (temConflitoReserva(idEspaco, dataInicio, horas)) {
                System.out.println("❌ Conflito de horário: Espaço já reservado neste período");
                return false;
            }

            double valorTotal = espaco.getValorHora() * horas;
            double valorComDesconto = usuario.calcularDesconto(valorTotal);

            // Verificar saldo
            if (usuario.getSaldo() < valorComDesconto) {
                System.out.println("❌ Saldo insuficiente");
                return false;
            }

            // Criar reserva
            String reservaId = "RES" + System.currentTimeMillis() + random.nextInt(1000);
            Reserva reserva = new Reserva(reservaId, idEspaco, usuario.getEmail(),
                    dataInicio, horas, valorTotal, valorComDesconto);

            reservas.add(reserva);
            espaco.setDisponivel(false);
            usuario.setSaldo(usuario.getSaldo() - valorComDesconto);

            // Registrar no histórico
            usuario.getHistoricoAlugueis().add(String.format(
                    "Reserva %s: %s - %d horas - R$ %.2f (Desconto: R$ %.2f)",
                    reservaId, espaco.getNome(), horas, valorComDesconto, valorTotal - valorComDesconto
            ));

            System.out.println("✅ Reserva realizada com sucesso!");
            System.out.printf("📋 Detalhes: %s | %d horas | Total: R$ %.2f%n",
                    espaco.getNome(), horas, valorComDesconto);
            return true;
        }

        System.out.println("❌ Espaço não encontrado ou indisponível");
        return false;
    }

    private boolean temConflitoReserva(String idEspaco, LocalDateTime dataInicio, int horas) {
        LocalDateTime dataFim = dataInicio.plusHours(horas);

        return reservas.stream()
                .filter(r -> r.getIdEspaco().equals(idEspaco) && r.getStatus().equals("CONFIRMADA"))
                .anyMatch(r -> {
                    LocalDateTime rInicio = r.getDataInicio();
                    LocalDateTime rFim = r.getDataFim();
                    return (dataInicio.isBefore(rFim) && dataFim.isAfter(rInicio));
                });
    }

    public List<Reserva> getReservasUsuario(String emailUsuario) {
        return reservas.stream()
                .filter(r -> r.getEmailUsuario().equals(emailUsuario))
                .toList();
    }
}
