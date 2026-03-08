package org.apache.ibatis.mapper;


import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/12
 * </p>
 */
public interface ParamTestMapper {

    void oneParamNoAnnotation(String name);

    void multiParamNoAnnotation(String name, Integer age);

    void withParamAnnotation(@Param("id") Long id, @Param("name") String name);

    void partialParamAnnotation(@Param("id") Long id, String name);

    void oneCollectionParam(List<Long> ids);
}
