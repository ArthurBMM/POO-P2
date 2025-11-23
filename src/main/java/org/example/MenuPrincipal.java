package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public // Menu Principal
class MenuPrincipal {
    private SistemaCoworking sistema;
    private Scanner scanner;
    private Pessoa usuarioLogado;

    public MenuPrincipal(SistemaCoworking sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public void acessarSistema() {
        limparConsole();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║            🏢 OPENOFFICE COWORKING               ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║                                                  ║");
        System.out.println("║  1. 📝 Cadastrar                                 ║");
        System.out.println("║  2. 🔐 Login                                     ║");
        System.out.println("║  3. ❌ Sair                                      ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\n👉 Escolha uma opção: ");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> login();
            case 3 -> sair();
            default -> {
                System.out.println("❌ Opção inválida!");
                acessarSistema();
            }
        }
    }

    private void cadastrar() {
        limparConsole();
        System.out.println("\n=== 📝 CADASTRO DE USUÁRIO ===");

        System.out.print("Digite seu nome: ");
        String nome = scanner.nextLine();

        System.out.print("Digite seu email: ");
        String email = scanner.nextLine();

        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        mostrarPlanosAssinatura();

        System.out.print("Deseja realizar alguma assinatura? (sim/nao): ");
        String resposta = scanner.nextLine();

        Pessoa novoUsuario;

        if (resposta.equalsIgnoreCase("sim")) {
            System.out.print("Qual plano deseja? (Visitante/Cliente+/VIP): ");
            String plano = scanner.nextLine();

            switch (plano.toLowerCase()) {
                case "cliente+" -> novoUsuario = new ClienteMais();
                case "vip" -> novoUsuario = new Vip();
                default -> novoUsuario = new Visitante();
            }
        } else {
            novoUsuario = new Visitante();
        }

        // Configurar usuário
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);
        novoUsuario.setAssinatura(novoUsuario.getClass().getSimpleName());

        System.out.print("Digite seu CEP (apenas números): ");
        String cep = scanner.nextLine();
        novoUsuario.setCep(cep);

        // Buscar endereço
        novoUsuario.buscarEnderecoPorCEP();
        novoUsuario.salvarDadosUsuario();

        System.out.println("\n✅ Cadastro realizado com sucesso!");
        pausar();
        this.usuarioLogado = novoUsuario;
        menuPrincipal();
    }

    private void mostrarPlanosAssinatura() {
        System.out.println("\n📋 PLANOS DE ASSINATURA");
        System.out.println("═══════════════════════════════════════");
        System.out.println("┌──────────────┬─────────────┬─────────────┐");
        System.out.println("│   VISITANTE  │  CLIENTE +  │     VIP     │");
        System.out.println("├──────────────┼─────────────┼─────────────┤");
        System.out.println("│   0% desc.   │   5% desc.  │  100% desc. │");
        System.out.println("├──────────────┼─────────────┼─────────────┤");
        System.out.println("│ Sem áreas    │ Sem áreas   │Área exclus. │");
        System.out.println("│ exclusivas   │ exclusivas  │             │");
        System.out.println("├──────────────┼─────────────┼─────────────┤");
        System.out.println("│ Sem          │ Pref. Nvl 1 │ Pref. Nvl 2 │");
        System.out.println("│ preferência  │             │             │");
        System.out.println("├──────────────┼─────────────┼─────────────┤");
        System.out.println("│   GRATUITO   │ R$ 39,90/D  │ R$ 69,90/D  │");
        System.out.println("│              │ R$ 1077,30/M│ R$ 1887,30/M│");
        System.out.println("└──────────────┴─────────────┴─────────────┘");
    }

    private void login() {
        limparConsole();
        System.out.println("=== 🔐 LOGIN ===");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        // Simulação de login - em produção, verificar no arquivo/banco
        System.out.println("⚠️  Funcionalidade de login completa requer integração com banco de dados");
        System.out.println("📧 Email digitado: " + email);

        // Para demonstração, criamos um usuário visitante
        this.usuarioLogado = new Visitante();
        usuarioLogado.setEmail(email);
        usuarioLogado.setNome("Usuário Demo");
        usuarioLogado.setSaldo(1000.0);

        System.out.println("✅ Login simulado com sucesso!");
        pausar();
        menuPrincipal();
    }

    private void menuPrincipal() {
        while (usuarioLogado != null) {
            limparConsole();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                 🏢 OPENOFFICE COWORKING                     ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║ 👤 Usuário: " + String.format("%-45s", usuarioLogado.getNome()) + "║");
            System.out.println("║ 💰 Saldo: R$ " + String.format("%-42.2f", usuarioLogado.getSaldo()) + "║");
            System.out.println("║ ⭐ Plano: " + String.format("%-44s", usuarioLogado.getAssinatura()) + "║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                              ║");
            System.out.println("║  1. 🏢 Alugar Espaços                                        ║");
            System.out.println("║  2. 📅 Minhas Reservas                                       ║");
            System.out.println("║  3. 💰 Adicionar Saldo                                       ║");
            System.out.println("║  4. 📋 Meus Benefícios                                       ║");
            System.out.println("║  5. 📊 Relatórios                                            ║");
            System.out.println("║  6. 🚪 Logout                                                ║");
            System.out.println("║  7. ❌ Sair do Sistema                                       ║");
            System.out.println("║                                                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.print("\n👉 Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1 -> alugarEspacos();
                case 2 -> minhasReservas();
                case 3 -> adicionarSaldo();
                case 4 -> mostrarBeneficios();
                case 5 -> mostrarRelatorios();
                case 6 -> logout();
                case 7 -> sair();
                default -> System.out.println("❌ Opção inválida!");
            }
        }
    }

    private void alugarEspacos() {
        limparConsole();
        System.out.println("=== 🏢 ESPAÇOS DISPONÍVEIS ===");

        List<Espaco> espacosDisponiveis = sistema.getReservas().getEspacosDisponiveis(usuarioLogado);

        if (espacosDisponiveis.isEmpty()) {
            System.out.println("❌ Nenhum espaço disponível no momento.");
            pausar();
            return;
        }

        // Mostrar espaços
        for (int i = 0; i < espacosDisponiveis.size(); i++) {
            Espaco espaco = espacosDisponiveis.get(i);
            String vipIcon = espaco.isExclusivoVIP() ? " 👑" : "";
            System.out.printf("%d. %s%s%n", i + 1, espaco.getNome(), vipIcon);
            System.out.printf("   📍 %s | 👥 %d pessoas | 💰 R$ %.2f/hora%n",
                    espaco.getLocalizacao(), espaco.getCapacidade(), espaco.getValorHora());
        }

        System.out.print("\n👉 Escolha o espaço (número): ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        if (escolha < 1 || escolha > espacosDisponiveis.size()) {
            System.out.println("❌ Escolha inválida!");
            pausar();
            return;
        }

        Espaco espacoEscolhido = espacosDisponiveis.get(escolha - 1);

        System.out.print("🕐 Quantas horas deseja alugar? ");
        int horas = scanner.nextInt();
        scanner.nextLine();

        // Data padrão para demonstração (agora + 1 hora)
        LocalDateTime dataInicio = LocalDateTime.now().plusHours(1);

        // Tentar reservar
        boolean sucesso = sistema.getReservas().reservarEspaco(
                espacoEscolhido.getId(), usuarioLogado, dataInicio, horas);

        pausar();
    }

    private void minhasReservas() {
        limparConsole();
        System.out.println("=== 📅 MINHAS RESERVAS ===");

        List<Reserva> reservas = sistema.getReservas().getReservasUsuario(usuarioLogado.getEmail());

        if (reservas.isEmpty()) {
            System.out.println("📭 Nenhuma reserva encontrada.");
        } else {
            for (int i = 0; i < reservas.size(); i++) {
                Reserva reserva = reservas.get(i);
                System.out.printf("%d. %s | %d horas | R$ %.2f | Status: %s%n",
                        i + 1, reserva.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                        reserva.getHoras(), reserva.getValorComDesconto(), reserva.getStatus());
            }
        }

        pausar();
    }

    private void adicionarSaldo() {
        limparConsole();
        System.out.println("=== 💰 ADICIONAR SALDO ===");
        System.out.printf("Saldo atual: R$ %.2f%n", usuarioLogado.getSaldo());

        System.out.print("Valor a adicionar: R$ ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        sistema.getPagamentos().processarPagamento(usuarioLogado, valor, "Recarga de saldo");
        pausar();
    }

    private void mostrarBeneficios() {
        limparConsole();
        System.out.println("=== 📋 SEUS BENEFÍCIOS ===");
        usuarioLogado.mostrarBeneficios();
        pausar();
    }

    private void mostrarRelatorios() {
        limparConsole();
        System.out.println("=== 📊 RELATÓRIOS ===");
        System.out.println("1. 📈 Relatório de Ocupação");
        System.out.println("2. 💵 Relatório Financeiro");
        System.out.println("3. 📋 Histórico de Pagamentos");
        System.out.print("\n👉 Escolha: ");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1 -> sistema.getRelatorios().gerarRelatorioOcupacao(sistema.getReservas());
            case 2 -> sistema.getRelatorios().gerarRelatorioFinanceiro(sistema.getPagamentos(), usuarioLogado);
            case 3 -> sistema.getPagamentos().mostrarHistoricoPagamentos(usuarioLogado);
            default -> System.out.println("❌ Opção inválida!");
        }

        pausar();
    }

    private void logout() {
        usuarioLogado = null;
        System.out.println("✅ Logout realizado com sucesso!");
        pausar();
        acessarSistema();
    }

    private void sair() {
        System.out.println("\n👋 Obrigado por usar o OpenOffice Coworking!");
        System.exit(0);
    }

    private void limparConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void pausar() {
        System.out.print("\n⏎ Pressione Enter para continuar...");
        scanner.nextLine();
    }
}
