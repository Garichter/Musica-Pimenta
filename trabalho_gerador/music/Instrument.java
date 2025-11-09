package music;

public enum Instrument {
    PIANO(0),
    ORGAO(16),
    XILOFONE(13),
    ACORDEON(21),
    VIOLAO(24),
    GUITARRA(26),
    BAIXO(32),
    VIOLINO(40),
    HARPA(46),
    STRINGS(48),
    TROMPETE(56),
    TUBA(58),
    PICCOLO(72),
    FLAUTA(73),
    ASSOBIO(78),
    GOBLIN(101),
    TEMAESTRELA(103),
    PASSARINHO(123),
    TELEFONE(124),
    TIRO(127);

    private final int codigo;

    Instrument(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static Instrument fromCodigo(int codigo) {
        for (Instrument i : Instrument.values()) {
            if (i.getCodigo() == codigo) {
                return i;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }

    public static Instrument random() {
        Instrument[] instrumentos = values();
        int index = java.util.concurrent.ThreadLocalRandom.current().nextInt(instrumentos.length);
        return instrumentos[index];
    }
}
