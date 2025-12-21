# Telegram Bot Spring Boot Starter 🤖🚀

Este es un **Spring Boot Starter** personalizado para integrar múltiples bots de Telegram mediante Webhooks de forma automática. Olvídate de configurar controladores, colas de mensajes o túneles manualmente.

## ✨ Características
- **Auto-Configuración**: Solo añade la dependencia y empieza a trabajar.
- **Soporte Multi-Bot**: Registra tantos bots como necesites.
- **Webhook ready**: Integración nativa para trabajar con servicios como Ngrok.
- **Gestión de Colas**: Procesamiento de mensajes eficiente mediante `MessageQueueService`.
- **Simplificado**: Solo extiende de una clase y ya tienes un bot funcional.

## 📦 Instalación

Añade la siguiente dependencia a tu proyecto pom.xml:
XML

```xml
<dependency>
    <groupId>io.github.yoneljorge</groupId>
    <artifactId>telegrambot-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

O si usas Gradle:
Groovy
```groovy
implementation 'io.github.yoneljorge:telegrambot-spring-boot-starter:0.0.1'
```

## 🚀 Modo de Uso
### 1. Configuración

Configuracion global de telegram
```properties
telegram.path=/telegram
telegram.webhook.enabled=true
app.url=https://myweb.com #-> sin el '/' al final
```
Añade las credenciales de cada bot en el archivo application.properties o application.yml:
```properties
telegram.bot.[custom-bot].token=TU_TOKEN_AQUI
telegram.bot.[custom-bot].username=NOMBRE_DE_TU_BOT
```

### 2. Crear un Bot
Crea una clase, márcala con @TelegramBot y extiende de AbstractTelegrambot:
```java
@TelegramBot(
        prefix = "telegram.bot.clients",
        id = "clients"
)
public class ClientsBot extends AbstractTelegramBot {
    
}
```

## 🏗️ Estructura Interna

El starter configura automáticamente:
- WebhookController: Endpoint que recibe las actualizaciones de Telegram.
- TelegramPlatform: Gestor central de los bots registrados.
- MessageQueueService: Sistema interno para el manejo ordenado de mensajes.

Creado con ❤️ por devYonel