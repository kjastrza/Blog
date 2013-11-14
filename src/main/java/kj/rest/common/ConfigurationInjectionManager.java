package kj.rest.common;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Startup
@Singleton
public class ConfigurationInjectionManager {
    static final String INVALID_KEY = "Invalid key '{0}'";
    static final String MANDATORY_PARAM_MISSING = "No definition found for a mandatory configuration parameter : '{0}'";
    private final String BUNDLE_FILE_NAME = "application";
    private final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_FILE_NAME);

    @Produces
    @ConfigValue
    public String injectConfiguration(InjectionPoint ip) throws IllegalStateException {
        ConfigValue param = ip.getAnnotated().getAnnotation(ConfigValue.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        String value;
        try {
            value = bundle.getString(param.key());
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

