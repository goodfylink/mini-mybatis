package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * </p>
 *
 * @author jcyin
 * @since 2026/2/15
 * </p>
 */
public class CharacterTypeHandler extends BaseTypeHandler<Character> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Character parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public Character getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toCharacter(rs.getString(columnName));
    }

    @Override
    public Character getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toCharacter(rs.getString(columnIndex));
    }

    @Override
    public Character getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toCharacter(cs.getString(columnIndex));
    }

    private Character toCharacter(String value) {
        return value == null || value.isEmpty() ? null : value.charAt(0);
    }
}
