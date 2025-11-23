package org.example;

public class ClienteMais extends Pessoa {
    @Override
    public double calcularDesconto(double valorOriginal) {
        return valorOriginal * 0.95; // 5% de desconto
    }

    @Override
    public void mostrarBeneficios() {
        System.out.println("⭐ Benefícios Cliente+:");
        System.out.println("   • 5% de desconto em reservas");
        System.out.println("   • +2 horas extras no aluguel");
        System.out.println("   • Preferência nível 1");
        System.out.println("   • 💰 R$ 39,90 Diário | R$ 1.077,30 Mensal");
    }

    @Override
    public boolean temAcessoExclusivo() {
        return false;
    }
}
