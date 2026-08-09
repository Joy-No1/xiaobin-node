package com.xxl.job.admin.framework.model;

import lombok.Data;

/**
 * xxl job user
 *
 * @author xuxueli 2019-05-04 16:43:12
 */
@Data
public class XxlJobUser {
	
	private int id;
	private String username;		// 账号
	private String password;		// 密码
	private String token;			// 登录token
	private int role;				// 角色：0-普通用户、1-管理员
	private String permission;		// 权限：执行器ID列表，多个逗号分割

}
