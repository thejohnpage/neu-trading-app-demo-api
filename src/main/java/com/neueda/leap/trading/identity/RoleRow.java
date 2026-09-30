package com.neueda.leap.trading.identity;
/** Mutable MyBatis row for an RBAC role. */
public class RoleRow{private long roleId;private String roleName,description;private boolean systemRole;public RoleRow(){}public long getRoleId(){return roleId;}public void setRoleId(long v){roleId=v;}public String getRoleName(){return roleName;}public void setRoleName(String v){roleName=v;}public String getDescription(){return description;}public void setDescription(String v){description=v;}public boolean isSystemRole(){return systemRole;}public void setSystemRole(boolean v){systemRole=v;}}
