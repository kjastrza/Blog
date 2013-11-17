package kj.rest.common;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@SuppressWarnings("UnusedDeclaration")
@Startup
@Singleton
public class ConfigurationInjectionManager {
    private static final String INVALID_KEY = "Invalid key '{0}'";
    private static final String MANDATORY_PARAM_MISSING = "No definition found for a mandatory configuration parameter : '{0}'";
    private static final String BUNDLE_FILE_NAME = "application";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_FILE_NAME);

    @Produces
    @ConfigValue
    public String injectConfiguration(InjectionPoint ip) throws IllegalStateException {
        ConfigValue param = ip.getAnnotated().getAnnotation(ConfigValue.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        String value;
        try {
            value = BUNDLE.getString(param.key());
            if (value == null || value.trim().length() == 0) {
                if (param.mandatory())
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, param.key()));
                else
                    return param.defaultValue();
            }
            return value;
        } catch (MissingResourceException e) {
            if (param.mandatory())
                throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, param.key()));
            return MessageFormat.format(INVALID_KEY, param.key());
        }
    }
}

