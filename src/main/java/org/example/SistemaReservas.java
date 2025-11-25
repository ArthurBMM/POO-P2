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
        // Array com todas as capitais brasileiras
        String[] capitais = {
                "São Paulo", "Rio de Janeiro", "Belo Horizonte", "Brasília", "Salvador",
                "Fortaleza", "Recife", "Porto Alegre", "Curitiba", "Manaus",
                "Belém", "Goiânia", "São Luís", "Maceió", "Campo Grande",
                "Teresina", "Natal", "Cuiabá", "Aracaju", "Florianópolis",
                "João Pessoa", "Macapá", "Rio Branco", "Vitória", "Porto Velho",
                "Boa Vista", "Palmas"
        };

        // Para cada capital, criar 3 espaços: Sala de Reunião, Sala Normal e Sala VIP
        for (String capital : capitais) {
            String sigla = capital.substring(0, 3).toUpperCase();

            // Sala de Reunião (comum)
            espacos.add(new Espaco(
                    sigla + "REU01",
                    "Sala de Reuniões " + capital,
                    TipoEspaco.SALA_REUNIAO,
                    120.0,
                    "Centro",
                    capital,
                    8,
                    false
            ));

            // Sala Normal / Área Coworking
            espacos.add(new Espaco(
                    sigla + "COW01",
                    "Área Coworking " + capital,
                    TipoEspaco.AREA_COWORKING,
                    40.0,
                    "Centro",
                    capital,
                    20,
                    false
            ));

            // Sala VIP (exclusiva)
            espacos.add(new Espaco(
                    sigla + "VIP01",
                    "Sala VIP Executiva " + capital,
                    TipoEspaco.SALA_VIP,
                    300.0,
                    "Área Premium",
                    capital,
                    6,
                    true
            ));

            // Adicionar alguns espaços extras para capitais maiores
            if (capital.equals("São Paulo") || capital.equals("Rio de Janeiro") || capital.equals("Brasília")) {
                // Auditório para capitais grandes
                espacos.add(new Espaco(
                        sigla + "AUD01",
                        "Auditório " + capital,
                        TipoEspaco.AUDITORIO,
                        500.0,
                        "Centro Empresarial",
                        capital,
                        50,
                        false
                ));

                // Sala de Treinamento
                espacos.add(new Espaco(
                        sigla + "TRE01",
                        "Sala de Treinamento " + capital,
                        TipoEspaco.SALA_TREINAMENTO,
                        200.0,
                        "Centro",
                        capital,
                        25,
                        false
                ));
            }
        }

        // Espaços adicionais específicos para demonstração
        espacos.add(new Espaco(
                "SPAUD02",
                "Auditório Paulista",
                TipoEspaco.AUDITORIO,
                600.0,
                "Paulista",
                "São Paulo",
                80,
                false
        ));

        espacos.add(new Espaco(
                "RJCOP01",
                "Sala Copacabana",
                TipoEspaco.SALA_REUNIAO,
                180.0,
                "Copacabana",
                "Rio de Janeiro",
                10,
                false
        ));

        espacos.add(new Espaco(
                "BSBESQ01",
                "Sala Esplanada",
                TipoEspaco.ESCRITORIO_PRIVATIVO,
                250.0,
                "Esplanada",
                "Brasília",
                4,
                true
        ));

        System.out.println("✅ " + espacos.size() + " espaços carregados para " + capitais.length + " capitais");
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

