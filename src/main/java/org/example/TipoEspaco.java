package org.example;

enum TipoEspaco {
    SALA_REUNIAO("Sala de Reunião"),
    ESCRITORIO_PRIVATIVO("Escritório Privativo"),
    SALA_TREINAMENTO("Sala de Treinamento"),
    AUDITORIO("Auditório"),
    AREA_COWORKING("Área de Coworking"),
    SALA_VIP("Sala VIP");

    private final String descricao;

    TipoEspaco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}