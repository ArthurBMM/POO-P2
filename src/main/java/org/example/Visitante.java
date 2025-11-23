package org.example;

public class Visitante extends Pessoa {
    @Override
    public double calcularDesconto(double valorOriginal) {
        return valorOriginal; // Sem desconto
    }

    @Override
    public void mostrarBeneficios() {
        System.out.println("🎫 Benefícios Visitante:");
        System.out.println("   • 0% de desconto em reservas");
        System.out.println("   • Acesso básico aos espaços");
        System.out.println("   • Sem áreas exclusivas");
        System.out.println("   • 💰 Grátis");
    }

    @Override
    public boolean temAcessoExclusivo() {
        return false;
    }
}
