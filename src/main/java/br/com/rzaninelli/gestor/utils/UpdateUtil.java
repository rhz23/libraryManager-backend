package br.com.rzaninelli.gestor.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateUtil {

    /**
    * Métodos utilitários para atualizar valores de atributos nullsafe e sem redefinição
    *
    * @param getter     método que obtém o valor atual (pode ser um lambda)
    * @param setter     método que define o novo valor (pode ser um lambda)
    * @param newValue   valor vindo do DTO
    * @param <T>        tipo do atributo
    */

    public static <T> void updateIfNotNullAndIfChanged(Supplier<T> getter, Consumer<T> setter, T newValue) {
        if (newValue != null) {
            updateIfChanged(getter, setter, newValue);
        }
    }

    public static <T> void updateIfChanged(Supplier<T> getter, Consumer<T> setter, T newValue) {
        T oldValue = getter.get();
        if (!Objects.equals(newValue, oldValue)) {
            setter.accept(newValue);
        }
    }

}
