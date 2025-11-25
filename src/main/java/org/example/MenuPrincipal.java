package org.example;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
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
        System.out.println("║        🌆 Aluguel apenas em capitais!            ║");
        System.out.println("║                                                  ║");
        System.out.println("║  1. 📝 Cadastrar                                 ║");
        System.out.println("║  2. 🔐 Login                                     ║");
        System.out.println("║  3. 🏙️  Ver Capitais                             ║");
        System.out.println("║  4. ❌ Sair                                      ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("\n👉 Escolha uma opção: ");

        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (opcao) {
            case 1 -> cadastrar();
            case 2 -> login();
            case 3 -> mostrarCapitais();
            case 4 -> sair();
            default -> {
                System.out.println("❌ Opção inválida!");
                acessarSistema();
            }
        }
    }

    private void mostrarCapitais() {
        limparConsole();
        sistema.getVerificadorCidades().listarCapitais();
        pausar();
        acessarSistema();
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
        System.out.println("🔄 Buscando endereço...");
        novoUsuario.buscarEnderecoPorCEP();

        // Verificar se é capital
        if (novoUsuario.getCidade() != null) {
            boolean ehCapital = sistema.getVerificadorCidades().ehCapital(novoUsuario.getCidade());
            if (ehCapital) {
                System.out.println("✅ Cidade verificada: " + novoUsuario.getCidade() + " (Capital)");
            } else {
                System.out.println("⚠️  Atenção: " + novoUsuario.getCidade() + " não é uma capital.");
                System.out.println("📌 Você só poderá alugar espaços se mudar para uma capital.");
            }
        }

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
        String emailDigitado = scanner.nextLine().trim();

        System.out.print("Senha: ");
        String senhaDigitada = scanner.nextLine();

        if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
            System.out.println("❌ Email e senha não podem ser vazios.");
            pausar();
            return;
        }

        // Arquivo salvo em: usuarios/<email>.txt
        Path caminho = Path.of("usuarios", emailDigitado + ".txt");

        if (!Files.exists(caminho)) {
            System.out.println("❌ Usuário não encontrado!");
            System.out.println("📂 Arquivo esperado: " + caminho.toAbsolutePath());
            pausar();
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(caminho, StandardCharsets.UTF_8);

            String nome = null;
            String emailArquivo = null;
            String senhaArquivo = null;
            String assinatura = null;
            String cep = null;
            String cidade = null;
            double saldo = 0.0;

            for (String linha : linhas) {
                if (linha.startsWith("Nome: ")) {
                    nome = linha.substring("Nome: ".length()).trim();
                } else if (linha.startsWith("Email: ")) {
                    emailArquivo = linha.substring("Email: ".length()).trim();
                } else if (linha.startsWith("Senha: ")) {
                    senhaArquivo = linha.substring("Senha: ".length()).trim();
                } else if (linha.startsWith("Assinatura: ")) {
                    assinatura = linha.substring("Assinatura: ".length()).trim();
                } else if (linha.startsWith("CEP: ")) {
                    cep = linha.substring("CEP: ".length()).trim();
                } else if (linha.startsWith("Cidade: ")) {
                    cidade = linha.substring("Cidade: ".length()).trim();
                } else if (linha.startsWith("Saldo: ")) {
                    try {
                        saldo = Double.parseDouble(linha.substring("Saldo: ".length()).trim());
                    } catch (NumberFormatException e) {
                        saldo = 0.0;
                    }
                }
            }

            if (senhaArquivo == null || !senhaArquivo.equals(senhaDigitada)) {
                System.out.println("❌ Senha incorreta!");
                pausar();
                return;
            }

            // Descobrir o tipo de usuário pela assinatura
            Pessoa usuario;
            if (assinatura != null && assinatura.equalsIgnoreCase("VIP")) {
                usuario = new Vip();
            } else if (assinatura != null &&
                    (assinatura.equalsIgnoreCase("Cliente+") || assinatura.equalsIgnoreCase("Cliente +"))) {
                usuario = new ClienteMais();
            } else {
                // Qualquer coisa diferente vira Visitante
                usuario = new Visitante();
            }

            // Preencher os dados básicos
            usuario.setNome(nome != null ? nome : emailArquivo);
            usuario.setEmail(emailArquivo != null ? emailArquivo : emailDigitado);
            usuario.setAssinatura(assinatura != null ? assinatura : "Visitante");
            usuario.setCep(cep);
            usuario.setCidade(cidade);
            usuario.setSaldo(saldo);

            this.usuarioLogado = usuario;

            System.out.println("\n✅ Login realizado com sucesso!");
            System.out.println("👤 Usuário: " + usuario.getNome());
            System.out.println("📧 Plano: " + usuario.getAssinatura());
            if (usuario.getCidade() != null) {
                System.out.println("📍 Cidade: " + usuario.getCidade());
            }

            pausar();
            menuPrincipal();

        } catch (IOException e) {
            System.out.println("❌ Erro ao ler arquivo de usuário: " + e.getMessage());
            pausar();
        }
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
            System.out.println("║ 🌆 Cidade: " + String.format("%-43s", usuarioLogado.getCidade()) + "║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                              ║");
            System.out.println("║  1. 🏢 Alugar Espaços (" + usuarioLogado.getCidade() + ")              ║");
            System.out.println("║  2. 🗺️  Ver Espaços por Cidade                               ║");
            System.out.println("║  3. 📅 Minhas Reservas                                       ║");
            System.out.println("║  4. 💰 Adicionar Saldo                                       ║");
            System.out.println("║  5. 📋 Meus Benefícios                                       ║");
            System.out.println("║  6. 📊 Relatórios                                            ║");
            System.out.println("║  7. 🏙️  Ver Capitais                                         ║");
            System.out.println("║  8. 🚪 Logout                                                ║");
            System.out.println("║  9. ❌ Sair do Sistema                                       ║");
            System.out.println("║                                                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.print("\n👉 Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1 -> alugarEspacos();
                case 2 -> verEspacosPorCidade();
                case 3 -> minhasReservas();
                case 4 -> adicionarSaldo();
                case 5 -> mostrarBeneficios();
                case 6 -> mostrarRelatorios();
                case 7 -> mostrarCapitaisNoMenu();
                case 8 -> logout();
                case 9 -> sair();
                default -> System.out.println("❌ Opção inválida!");
            }
        }
    }

    private void alugarEspacos() {
        limparConsole();
        System.out.println("=== 🏢 ESPAÇOS DISPONÍVEIS EM " + usuarioLogado.getCidade().toUpperCase() + " ===");

        // Verificar se usuário está em capital
        if (!sistema.getVerificadorCidades().ehCapital(usuarioLogado.getCidade())) {
            System.out.println("❌ Apenas residentes de capitais podem alugar espaços.");
            System.out.println("📍 Sua cidade atual: " + usuarioLogado.getCidade());
            System.out.println("📌 Use a opção 'Ver Espaços por Cidade' para ver onde temos unidades.");
            pausar();
            return;
        }

        List<Espaco> espacosDisponiveis = sistema.getReservas().getEspacosDisponiveis(usuarioLogado);

        if (espacosDisponiveis.isEmpty()) {
            System.out.println("❌ Nenhum espaço disponível na sua cidade no momento.");
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

    private void verEspacosPorCidade() {
        limparConsole();
        System.out.println("=== 🗺️  ESPAÇOS POR CIDADE ===");

        System.out.print("Digite o nome da cidade: ");
        String cidade = scanner.nextLine();

        sistema.getReservas().mostrarEspacosPorCidade(cidade);
        pausar();
    }

    private void mostrarCapitaisNoMenu() {
        limparConsole();
        sistema.getVerificadorCidades().listarCapitais();
        pausar();
    }

    // ... (outros métodos permanecem iguais: minhasReservas, adicionarSaldo, mostrarBeneficios, mostrarRelatorios, etc.)

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
