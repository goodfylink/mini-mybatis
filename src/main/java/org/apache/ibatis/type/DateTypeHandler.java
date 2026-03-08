package org.apache.ibatis.type;

import java.sql.*;
import java.util.Date;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/15
 * </p>
 */
public class DateTypeHandler extends BaseTypeHandler<Date> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, new Timestamp(parameter.getTime()));
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toDate(rs.getTimestamp(columnName));
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toDate(rs.getTimestamp(columnIndex));
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toDate(cs.getTimestamp(columnIndex));
    }

    private Date toDate(Timestamp timestamp) {
        return timestamp == null ? null : new Date(timestamp.getTime());
    }

}
