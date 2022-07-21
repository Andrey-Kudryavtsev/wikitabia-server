package com.intabia.wikitabia.model.util;

import com.intabia.wikitabia.model.annotation.UserFriendlyName;
import javax.persistence.spi.PersistenceUnitInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;

/**
 * Проверяет наличие аннотации {@link UserFriendlyName @UserFriendlyName} у бинов
 * и добавляет соответствующие записи в словарь
 * {@link UserFriendlyNameTranslationTool UserFriendlyNameTranslationTool}.
 */
@Component
public class UserFriendlyNameBeanPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    UserFriendlyName userFriendlyName = bean.getClass().getAnnotation(UserFriendlyName.class);
    if (userFriendlyName != null) {
      UserFriendlyNameTranslationTool.add(bean.getClass().getSimpleName(),
          userFriendlyName.value());
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof EntityManagerFactoryInfo) {
      EntityManagerFactoryInfo emf = (EntityManagerFactoryInfo) bean;
      PersistenceUnitInfo info = emf.getPersistenceUnitInfo();
      if (info != null && info.getManagedClassNames() != null) {
        for (String name : info.getManagedClassNames()) {
          try {
            Class<?> clazz = Class.forName(name);
            UserFriendlyName userFriendlyName = clazz.getAnnotation(UserFriendlyName.class);
            if (userFriendlyName != null) {
              UserFriendlyNameTranslationTool.add(clazz.getSimpleName(), userFriendlyName.value());
            }
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Class %s not found", name));
          }
        }
      }
    }
    return bean;
  }
}
