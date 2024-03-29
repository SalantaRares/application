package com.application.utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.util.Arrays;

/**
 * Class created because the original hibernate(5.2) class have a bug
 * In class is a comment where the bug was fixed
 */
public class AliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {

    private final Class resultClass;
    private boolean isInitialized;
    private String[] aliases;
    private Setter[] setters;

    public AliasToBeanResultTransformer(Class resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        } else {
            this.isInitialized = false;
            this.resultClass = resultClass;
        }
    }

    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }


    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            if (!this.isInitialized) {
                this.initialize(aliases);
            } else {
                this.check(aliases);
            }

            Object result = this.resultClass.newInstance();

            for (int i = 0; i < aliases.length; ++i) {
                if (this.setters[i] != null) {
                    this.setters[i].set(result, tuple[i], null);
                }
            }
            return result;
        } catch (InstantiationException var5) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
        } catch (IllegalAccessException var6) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
        }
    }

    private void initialize(String[] aliases) {
        PropertyAccessStrategyBasicImpl propertyAccessStrategy = new PropertyAccessStrategyBasicImpl();
        this.aliases = new String[aliases.length];
        this.setters = new Setter[aliases.length];

        for (int i = 0; i < aliases.length; ++i) {
            String alias = aliases[i];
            if (alias != null) {
                this.aliases[i] = alias;
                alias = transformUnderlineInCamelCase(alias); // this line vas added to fix the bug
                this.setters[i] = propertyAccessStrategy.buildPropertyAccess(this.resultClass, alias).getSetter();
            }
        }
        this.isInitialized = true;
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException("aliases are different from what is cached; aliases=" + Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            AliasToBeanResultTransformer that = (AliasToBeanResultTransformer) o;
            if (!this.resultClass.equals(that.resultClass)) {
                return false;
            } else {
                return Arrays.equals(this.aliases, that.aliases);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.resultClass.hashCode();
        result = 31 * result + (this.aliases != null ? Arrays.hashCode(this.aliases) : 0);
        return result;
    }

    private String transformUnderlineInCamelCase(String string) {
        String camelcaseString = "";

        if (!string.contains("_")) {
            camelcaseString = string.toLowerCase();
        } else {
            String[] splitVector = string.split("_");
            for (String word : splitVector) {
                word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                camelcaseString = camelcaseString + word;
            }
        }
        return camelcaseString;
    }

}
