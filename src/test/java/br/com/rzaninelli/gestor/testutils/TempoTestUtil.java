package br.com.rzaninelli.gestor.testutils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TempoTestUtil {

    private static final ZoneId ZONE = ZoneId.systemDefault();
    private static Clock clock = Clock.systemDefaultZone();

    //Define um relógio fixo
    public static void fixarAgora(LocalDateTime agora) {
        clock = Clock.fixed(agora.atZone(ZONE).toInstant(), ZONE);
    }

    //Restaura o relógio para o sistema
    public static void restaurarAgora() {
        clock = Clock.systemDefaultZone();
    }

    //Retorna o "agora" de acordo com o relógio de teste
    public static LocalDateTime agora() {
        return LocalDateTime.now(clock);
    }

}
