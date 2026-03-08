package org.apache.ibatis.type;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for BooleanTypeHandler class
 *
 * @author jcyin
 * @since 2026/2/21
 */
public class BooleanTypeHandlerTest {

    private BooleanTypeHandler handler = new BooleanTypeHandler();

    @Test
    public void testSetParameter() throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);
        handler.setParameter(ps, 1, true, null);
        verify(ps).setBoolean(1, true);
    }

    @Test
    public void testGetResult() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getBoolean("column")).thenReturn(true);
        Boolean result = handler.getResult(rs, "column");
        assertTrue(result);
    }
}