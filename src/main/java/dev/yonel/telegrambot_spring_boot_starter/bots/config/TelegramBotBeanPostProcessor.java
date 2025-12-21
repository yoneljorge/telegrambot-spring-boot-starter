package dev.yonel.telegrambot_spring_boot_starter.bots.config;

import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.annotations.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBotProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

public class TelegramBotBeanPostProcessor implements BeanPostProcessor {
    private final Environment environment;

    public TelegramBotBeanPostProcessor(Environment environment){
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {
        if(bean instanceof AbstractTelegramBot bot){
            Class<?> beanClass = bean.getClass();
            if(beanClass.isAnnotationPresent(TelegramBot.class)){
                TelegramBot annotation = beanClass.getAnnotation(TelegramBot.class);

                // Verificamos si tiene un context-path
                String contextPath = null;
                try {
                     contextPath = environment.getProperty("server.servlet.context-path");
                }catch (Exception e){

                }
                // Extraemos los valores
                TelegramBotProperties props = new TelegramBotProperties();
                props.setId(annotation.id());
                props.setToken(environment.getProperty(annotation.prefix() + ".token"));
                props.setUsername(environment.getProperty(annotation.prefix() + ".username"));
                if(contextPath != null && !contextPath.isEmpty()){
                    props.setPath(contextPath +
                            environment.getProperty("telegram.path") + "/" +
                            environment.getProperty(annotation.prefix() + ".token"));
                }else{
                    props.setPath(environment.getProperty("telegram.path") + "/" +
                            environment.getProperty(annotation.prefix() + ".token"));
                }
                bot.setBotProps(props);
            }
        }
        return bean;
    }
}
