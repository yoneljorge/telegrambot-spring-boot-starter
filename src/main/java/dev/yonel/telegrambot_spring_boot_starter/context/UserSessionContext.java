package dev.yonel.telegrambot_spring_boot_starter.context;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mantiene datos vivos por usuario y por bot.
 *
 * Esta clase proporciona un contexto para almacenar información específica de
 * la sesión de un usuario para diferentes bots durante la interacción con el
 * chatbot.
 */
public class UserSessionContext {
    // Mapa que agrupa la información por botId
    private final Map<String, BotSession> botSessions = new HashMap<>();

    // Método auxiliar para obtener o crear la sesión de un bot específico
    public BotSession getBotSession(TelegramBot bot) {
        return botSessions.computeIfAbsent(bot.getId(), k -> new BotSession());
    }

    // Clase interna para agrupar los datos por bot
    public static class BotSession {
        private List<String> flowList = new ArrayList<>();
        @Getter
        @Setter
        private int step = 0;
        private final Map<String, TypedValue<?>> data = new HashMap<>();

        /*
         * Lista que agrupa la información de los id de los mensajes que van a ser
         * eliminados.
         */
        private List<Integer> messagesToDelete = new ArrayList<>();
        @Getter
        private boolean pendingMessageToDelete = false;

        private static class TypedValue<T> {
            private final T value;
            @SuppressWarnings("unused")
            private final Class<T> type;

            public TypedValue(T value, Class<T> type) {
                this.value = value;
                this.type = type;
            }

        }

        /**
         * Obtiene el nombre del flujo de conversación activo para este usuario.
         *
         * @return El nombre del flujo activo.
         */
        public String getActiveFlow() {
            if (flowList.isEmpty()) {
                return ""; // Si no hay flujo activo, retorna una cadena vacía
            }
            // Retorna el último flujo activo, que es el más reciente agregado
            return flowList.getLast();
        }

        /**
         * Establece el flujo de conversación activo para este usuario.
         * Al establecer un nuevo flujo, se reinicia el paso actual a 0.
         *
         * @param activeFlow El nombre del flujo que se activa.
         */
        public BotSession setActiveFlow(String activeFlow) {
            this.flowList.add(activeFlow);
            this.step = 0;
            return this;
        }

        /**
         * Elimina el último flujo de conversación activo para este usuario.
         * Al eliminar un flujo de conversación, se reinicia el paso actual a 0.
         */
        public BotSession removeLastActiveFlow() {
            if (!flowList.isEmpty()) {
                flowList.removeLast();
            }
            this.step = 0;
            return this;
        }

        /**
         * Incrementa el paso actual dentro del flujo de conversación activo.
         * Se utiliza para avanzar a la siguiente etapa lógica dentro de un flujo.
         */
        public BotSession nextStep() {
            this.step++;
            return this;
        }

        /**
         * Se reinicia el paso actual a 0.
         */
        public BotSession resetStep() {
            this.step = 0;
            return this;
        }

        /**
         * Restablece el contexto de la sesión del usuario.
         * Esto implica establecer el flujo activo a `null`, el paso actual a 0 y
         * eliminar todos los datos temporales almacenados.
         */
        public BotSession reset() {
            this.flowList.clear();
            this.step = 0;
            this.data.clear();
            return this;
        }

        /**
         * Almacena un dato temporal asociado a una clave específica dentro del contexto
         * de la sesión del usuario.
         *
         * @param key   La clave con la que se identificará el dato.
         * @param value El objeto que se desea almacenar.
         */
        @SuppressWarnings("unchecked")
        public <T> BotSession putData(SessionAttribute key, T value) {
            if (value == null) {
                throw new IllegalArgumentException("El valor no puede ser nulo");
            }
            data.put(key.value(), new TypedValue<>(value, (Class<T>) value.getClass()));
            return this;
        }

        /**
         * Recupera un dato temporal almacenado en el contexto de la sesión del usuario
         * utilizando su clave.
         *
         * @param key La clave del dato que se desea obtener.
         * @return El objeto almacenado bajo la clave especificada, o `null` si la clave
         *         no existe.
         */
        @SuppressWarnings("unchecked")
        public <T> T getData(SessionAttribute key) {
            TypedValue<?> typedValue = data.get(key.value());

            if (typedValue == null) {
                return null; // Si no existe el dato, retorna null
            }

            try {
                return (T) typedValue.value; // Retorna el valor almacenado, casteado al tipo esperado
            } catch (ClassCastException e) {
                throw new IllegalStateException("Tipo de dato no coincide para la clave: " + key.value(), e);
            }
        }

        public <T> BotSession removeData(SessionAttribute key) {
            data.remove(key.value());
            return this;
        }

        public BotSession cleanData() {
            this.data.clear();
            return this;
        }

        /**
         * Agregamos cada mensaje que va a ser eliminado.
         * 
         * @param messageId id del mensaje a eliminar
         * 
         */
        public BotSession setMessageIdToDelete(Integer messageId) {
            messagesToDelete.add(messageId);
            pendingMessageToDelete = true;
            return this;
        }

        /**
         * Retornamos la lista de mensajes a eliminar.
         * 
         * @return la lista MessageId a eliminar.
         */
        public List<Integer> getMessagesIdToDelete() {
            List<Integer> newList = new ArrayList<>();
            for(int messageid : messagesToDelete){
                newList.add(messageid);
            }
            
            pendingMessageToDelete = false;
            messagesToDelete.clear();

            return newList;
        }
    }
}
