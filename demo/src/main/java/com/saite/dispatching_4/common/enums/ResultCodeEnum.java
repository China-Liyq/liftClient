package com.saite.dispatching_4.common.enums;

/**
 * 返回编码枚举
 * @author yangjian
 */
public enum ResultCodeEnum {
	/**返回编码*/
	SUCCESS("1000","处理成功"),
	SYS_ERR("1001","系统处理异常"),
	DATA_EMPTY_ERR("1002","数据为空"),
	DATA_FAULT_ERR("1003","数据错误"),
	DATA_CHECK_ERR("1004","数据校验错误"),
	LOGIN_ERR("1005","登录失败"),
	DATA_EXIST_ERR("1006", "数据已存在"),
	CREATE_DIR_ERR("1201","创建文件目录失败"),
	ADD_FILE_ERR("1202","保存文件异常"),
	FILE_EXIST_ERR("1203","文件已存在"),
	FILE_NOT_EXIST_ERR("1204","文件不存在"),
	DEL_FILE_ERR("1205","删除文件异常"),
	FILE_LEN_LIMIT_ERR("1203","文件大小超出限制"),

	UNAUTHORIZED("1401","token校验失败"),
	TOKEN_EMPTY("1402","token不能为空"),

	FEIGN_CALL_ERR("1501","内部服务调用异常"),
	SERVICE_EXCEPTION("2000","业务异常"),
	DB_ERR("3000","数据库处理异常"),
	NETWORK_ERR("4000","网络异常"),
	LOGOUT_ERR("5000","注销失败"),
	ROBOT_OFFLINE("6000","机器人已离线"),
	LIFT_CONTROL_OFFLINE("6001","梯控已离线"),
	ROBOT_MSG_STATUS_ERROR("6100","机器人返回消息状态异常"),
	CONCURRENCY_DOWNLOAD_LIMIT_ERROR("7000","并发下载限制异常"),
	WEBSOCKET_ERR("8000","websocket消息处理异常"),

	//------------------用户端错误  A0001----------------------//
	USER_NAME_ALREADY_EXISTS("A0301","权限不足,无修改个人关联项目权限!"),

	//------------------用户登录异常 A0200----------------------//

	//------------------访问权限异常 A0300----------------------//
	NO_PERMISSION("A0301","权限不足,请联系管理员!"),
	NO_PERMISSION_MODIFY_PERSONAL_PROJECTS("A0302","权限不足,无修改个人关联项目权限!"),
	NO_PERMISSION_MODIFY_PERSONAL_RENEWAL("A0303","权限不足,无修改个人续期权限!"),
	NO_PERMISSION_DELETE_PERSONAL_ACCOUNT("A0304","权限不足,无删除个人账号权限!"),
	NO_PERMISSION_MODIFY_CURRENT_ACCOUNT_ROLE("A0305","权限不足,无修改当前账号角色权限!"),
	NO_PERMISSION_DELETE_ROBOT("A0305","权限不足,无删除设备权限!"),

	//------------------用户请求参数错误 A0400----------------------//
	PARAMETER_IS_NULL("A0401","请求参数异常!"),
	PARAMETER_DEVICE_ID_IS_NULL("A0402","请求必填参数设备ID为空!"),
	PARAMETER_DEVICE_TYPE_IS_NULL("A0402","请求必填参数设备类型为空!"),
	PARAMETER_PROJECT_ID_IS_NULL("A0403","请求必填参数项目ID为空!"),
	PARAMETER_USER_ID_IS_NULL("A0403","请求必填参数用户ID为空!"),


	//------------------用户请求服务异常 A0500----------------------//
	CMD_FAIL_TRAFFIC_LIGHT_CROSSROAD_("A0501","下发指令失败：通行红绿灯路口任务中"),

	//------------------用户文件异常 A0700----------------------//
	FILE_UPDATE_FAILED("A0701","更新失败!"),
	FUF_DEVICE_OFFLINE("A0702","更新失败,设备离线中!"),
	FUF_DEVICE_NOT_STANDBY("A0703","更新失败,设备非待机中!"),
	FUF_OTHER_USER_CONTROL("A0704","更新失败,其他用户控制设备中!"),

	FUF_FILE_OVER_MAX_SIZE("A0711","更新失败,文件大小超上限!"),
	FUF_SEND_FILE_FAILED("A0712","更新失败,下发文件失败!"),
	FUF_HAVE_DOWNLOAD_TASK("A0713","更新失败,设备执行下载任务中!"),
	FUF_SOFTWARE_NOT_EXIST("A0714","更新失败,升级文件不存在,!"),
	FUF_SOFTWARE_UPGRADING("A0715","更新失败,设备软件升级中!"),
	FUF_CONFIG_UPGRADING("A0716","更新失败,设备配置更新中!"),
	FUF_SAVE_FILE_FAILED("A0717","更新失败,保存文件失败!"),


	//------------------系统执行出错 B0001----------------------//
	SYSTEM_EXECUTION_TIMEOUT("B0101","系统执行超时!"),


	//------------------调用第三方服务出错 C0001----------------------//

	;
	
	private final String code;
	private final String msg;
	
	ResultCodeEnum(String code, String msg) {
		this.code=code;
		this.msg=msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
