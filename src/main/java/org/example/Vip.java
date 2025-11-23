package org.example;

public class Vip extends Pessoa {
    @Override
    public double calcularDesconto(double valorOriginal) {
        return valorOriginal * 0.0; // 100% de desconto no primeiro aluguel do dia
    }

    @Override
    public void mostrarBeneficios() {
        System.out.println("👑 Benefícios VIP:");
        System.out.println("   • 100% de desconto no primeiro aluguel do dia");
        System.out.println("   • Acesso à área VIP exclusiva");
        System.out.println("   • Preferência nível 2 em eventos");
        System.out.println("   • Horas ilimitadas");
        System.out.println("   • Descontos exclusivos em eventos");
        System.out.println("   • 💰 R$ 69,90 Diário | R$ 1.887,30 Mensal");
    }

    @Override
    public boolean temAcessoExclusivo() {
        return true;
    }
}