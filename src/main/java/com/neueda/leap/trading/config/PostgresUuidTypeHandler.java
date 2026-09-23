package com.neueda.leap.trading.config;

import java.sql.*;
import java.util.UUID;
import org.apache.ibatis.type.*;

/** PostgreSQL UUID type handler for MyBatis parameter and result mappings. */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(UUID.class)
public class PostgresUuidTypeHandler extends BaseTypeHandler<UUID> {
 @Override public void setNonNullParameter(PreparedStatement ps,int i,UUID value,JdbcType jdbcType)throws SQLException{ps.setObject(i,value,Types.OTHER);}
 @Override public UUID getNullableResult(ResultSet rs,String column)throws SQLException{return uuid(rs.getObject(column));}
 @Override public UUID getNullableResult(ResultSet rs,int column)throws SQLException{return uuid(rs.getObject(column));}
 @Override public UUID getNullableResult(CallableStatement cs,int column)throws SQLException{return uuid(cs.getObject(column));}
 private UUID uuid(Object value){if(value==null)return null;if(value instanceof UUID u)return u;return UUID.fromString(value.toString());}
}
