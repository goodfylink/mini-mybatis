package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {


    String value();
}
