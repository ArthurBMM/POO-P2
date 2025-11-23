package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VerificadorCidades {
    private Set<String> capitais;
    private static VerificadorCidades instance;

    private VerificadorCidades() {
        carregarCapitais();
    }

    public static VerificadorCidades getInstance() {
        if (instance == null) {
            instance = new VerificadorCidades();
        }
        return instance;
    }

    private void carregarCapitais() {
        try {
            Path caminho = Path.of("capitais_brasil.txt");
            if (Files.exists(caminho)) {
                capitais = new HashSet<>(Files.readAllLines(caminho, StandardCharsets.UTF_8));
                System.out.println("✅ Capitais carregadas: " + capitais.size() + " cidades");
            } else {
                // Criar arquivo com capitais padrão se não existir
                capitais = new HashSet<>(Arrays.asList(
                        "São Paulo", "Rio de Janeiro", "Belo Horizonte", "Brasília", "Salvador",
                        "Fortaleza", "Recife", "Porto Alegre", "Curitiba", "Manaus",
                        "Belém", "Goiânia", "São Luís", "Maceió", "Campo Grande",
                        "Teresina", "Natal", "Cuiabá", "Aracaju", "Florianópolis",
                        "João Pessoa", "Macapá", "Rio Branco", "Vitória", "Porto Velho",
                        "Boa Vista", "Palmas"
                ));
                salvarCapitais();
            }
        } catch (IOException e) {
            System.err.println("❌ Erro ao carregar capitais: " + e.getMessage());
            capitais = new HashSet<>();
        }
    }

    private void salvarCapitais() {
        try {
            Path caminho = Path.of("capitais_brasil.txt");
            Files.write(caminho, capitais, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Erro ao salvar capitais: " + e.getMessage());
        }
    }

    public boolean ehCapital(String cidade) {
        if (cidade == null) return false;
        return capitais.stream()
                .anyMatch(capital -> capital.equalsIgnoreCase(cidade.trim()));
    }

    public boolean cidadesIguais(String cidade1, String cidade2) {
        if (cidade1 == null || cidade2 == null) return false;
        return cidade1.equalsIgnoreCase(cidade2);
    }

    public void listarCapitais() {
        System.out.println("🏙️  CAPITAIS BRASILEIRAS:");
        capitais.forEach(capital -> System.out.println("  • " + capital));
    }
}
