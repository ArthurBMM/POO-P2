package org.example;

import java.time.LocalDateTime;
import java.util.*;
public class SistemaReservas {
    private List<Espaco> espacos;
    private List<Reserva> reservas;
    private Random random;
    private VerificadorCidades verificadorCidades;

    public SistemaReservas(VerificadorCidades verificadorCidades) {
        this.espacos = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.random = new Random();
        this.verificadorCidades = verificadorCidades;
        carregarEspacosIniciais();
    }

    private void carregarEspacosIniciais() {
        // Espaços em São Paulo
        espacos.add(new Espaco("SALA01", "Sala de Reuniões Pequena", TipoEspaco.SALA_REUNIAO, 80.0, "1º Andar", "São Paulo", 6, false));
        espacos.add(new Espaco("SALA02", "Sala de Reuniões Média", TipoEspaco.SALA_REUNIAO, 120.0, "1º Andar", "São Paulo", 10, false));
        espacos.add(new Espaco("SALA03", "Sala de Reuniões Grande", TipoEspaco.SALA_REUNIAO, 200.0, "2º Andar", "São Paulo", 15, false));
        espacos.add(new Espaco("AUD01", "Auditório Principal", TipoEspaco.AUDITORIO, 500.0, "Térreo", "São Paulo", 50, false));

        // Espaços no Rio de Janeiro
        espacos.add(new Espaco("RIO01", "Sala Copacabana", TipoEspaco.SALA_REUNIAO, 150.0, "Beira Mar", "Rio de Janeiro", 8, false));
        espacos.add(new Espaco("RIO02", "Auditório Rio", TipoEspaco.AUDITORIO, 450.0, "Centro", "Rio de Janeiro", 40, false));

        // Espaços em Brasília
        espacos.add(new Espaco("BSB01", "Sala Esplanada", TipoEspaco.SALA_REUNIAO, 180.0, "Setor Comercial", "Brasília", 12, false));

        // Espaços em Belo Horizonte
        espacos.add(new Espaco("BH01", "Sala Savassi", TipoEspaco.SALA_REUNIAO, 90.0, "Savassi", "Belo Horizonte", 6, false));

        // Espaços exclusivos VIP
        espacos.add(new Espaco("VIP01", "Sala VIP Executiva", TipoEspaco.SALA_VIP, 300.0, "3º Andar", "São Paulo", 8, true));
        espacos.add(new Espaco("VIP02", "Sala de Reuniões Premium", TipoEspaco.SALA_REUNIAO, 400.0, "3º Andar", "Rio de Janeiro", 12, true));
        espacos.add(new Espaco("VIP03", "Escritório Privativo VIP", TipoEspaco.ESCRITORIO_PRIVATIVO, 200.0, "3º Andar", "Brasília", 4, true));
    }

    public List<Espaco> getEspacosDisponiveis(Pessoa usuario) {
        return espacos.stream()
                .filter(espaco -> espaco.isDisponivel() &&
                        (!espaco.isExclusivoVIP() || usuario.temAcessoExclusivo()) &&
                        verificadorCidades.cidadesIguais(espaco.getCidade(), usuario.getCidade())) // Filtro por cidade
                .toList();
    }

    public List<Espaco> getTodosEspacos() {
        return espacos;
    }

    public List<Espaco> getEspacosPorCidade(String cidade) {
        return espacos.stream()
                .filter(espaco -> verificadorCidades.cidadesIguais(espaco.getCidade(), cidade))
                .toList();
    }

    public boolean reservarEspaco(String idEspaco, Pessoa usuario, LocalDateTime dataInicio, int horas) {
        // Verificar se usuário está em capital
        if (!verificadorCidades.ehCapital(usuario.getCidade())) {
            System.out.println("❌ Apenas residentes de capitais podem alugar espaços.");
            System.out.println("📍 Sua cidade: " + usuario.getCidade());
            return false;
        }

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

            // Verificar se está na mesma cidade
            if (!verificadorCidades.cidadesIguais(espaco.getCidade(), usuario.getCidade())) {
                System.out.println("❌ Você só pode alugar espaços na sua cidade (" + usuario.getCidade() + ")");
                System.out.println("📍 Este espaço fica em: " + espaco.getCidade());
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
                    "Reserva %s: %s - %d horas - R$ %.2f (Desconto: R$ %.2f) - %s",
                    reservaId, espaco.getNome(), horas, valorComDesconto, valorTotal - valorComDesconto, espaco.getCidade()
            ));

            System.out.println("✅ Reserva realizada com sucesso!");
            System.out.printf("📋 Detalhes: %s | %d horas | Total: R$ %.2f | Cidade: %s%n",
                    espaco.getNome(), horas, valorComDesconto, espaco.getCidade());
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

    public void mostrarEspacosPorCidade(String cidade) {
        List<Espaco> espacosCidade = getEspacosPorCidade(cidade);
        if (espacosCidade.isEmpty()) {
            System.out.println("❌ Nenhum espaço encontrado para " + cidade);
            return;
        }

        System.out.println("🏢 ESPAÇOS EM " + cidade.toUpperCase());
        for (int i = 0; i < espacosCidade.size(); i++) {
            Espaco espaco = espacosCidade.get(i);
            String status = espaco.isDisponivel() ? "🟢" : "🔴";
            String vipIcon = espaco.isExclusivoVIP() ? " 👑" : "";
            System.out.printf("%s %d. %s%s%n", status, i + 1, espaco.getNome(), vipIcon);
            System.out.printf("   📍 %s | 👥 %d pessoas | 💰 R$ %.2f/hora%n",
                    espaco.getLocalizacao(), espaco.getCapacidade(), espaco.getValorHora());
        }
    }
}

