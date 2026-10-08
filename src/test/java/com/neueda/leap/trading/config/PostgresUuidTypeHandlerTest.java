package com.neueda.leap.trading.config;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.*;
import java.util.UUID;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Test;
class PostgresUuidTypeHandlerTest {
 private final PostgresUuidTypeHandler handler=new PostgresUuidTypeHandler();
 @Test void bindsNativeUuidAsPostgresOther() throws Exception {
  PreparedStatement ps=mock(PreparedStatement.class);UUID id=UUID.randomUUID();
  handler.setNonNullParameter(ps,2,id,JdbcType.OTHER);
  verify(ps).setObject(2,id,Types.OTHER);
 }
 @Test void readsUuidFromResultSetByName() throws Exception {
  ResultSet rs=mock(ResultSet.class);UUID id=UUID.randomUUID();
  when(rs.getObject("id")).thenReturn(id);
  assertEquals(id,handler.getNullableResult(rs,"id"));
 }
 @Test void readsUuidFromResultSetByIndexAndStringValue() throws Exception {
  ResultSet rs=mock(ResultSet.class);UUID id=UUID.randomUUID();
  when(rs.getObject(1)).thenReturn(id.toString());
  assertEquals(id,handler.getNullableResult(rs,1));
 }
 @Test void readsCallableStatementAndNull() throws Exception {
  CallableStatement cs=mock(CallableStatement.class);UUID id=UUID.randomUUID();
  when(cs.getObject(1)).thenReturn(id);
  when(cs.getObject(2)).thenReturn(null);
  assertEquals(id,handler.getNullableResult(cs,1));
  assertNull(handler.getNullableResult(cs,2));
 }
}
