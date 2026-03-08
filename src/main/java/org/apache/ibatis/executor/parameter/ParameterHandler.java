package org.apache.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/14
 * </p>
 */
public interface ParameterHandler {

    Object getParameterObject();

    void setParameters(PreparedStatement ps) throws SQLException;

}