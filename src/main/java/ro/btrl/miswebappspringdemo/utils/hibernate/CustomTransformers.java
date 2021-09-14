package ro.btrl.miswebappspringdemo.utils.hibernate;

import org.hibernate.transform.ResultTransformer;

/**
 * Created because the hibernate AliasToBeanResultTransformer class had a bug
 */
public class CustomTransformers {

    private CustomTransformers() {
    }

    public static ResultTransformer aliasToBean(Class target) {
        return new AliasToBeanResultTransformer(target);
    }

}
