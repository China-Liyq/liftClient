package com.saite.dispatching_4.common.bean.page;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saite.dispatching_4.common.enums.ResultCodeEnum;

import java.util.Collections;

/**
 * 后端交互返回结果对象
 * @author yangjian
 */
public class WrapperResult {

	/**结果常量*/
	private ResultCodeEnum resultCode;
	/**数据*/
	private Object data;
	/**消息*/
	private String message;
	/**编码*/
	private String code;

	public WrapperResult() {}

	public WrapperResult(String message, ResultCodeEnum resultCode, Object data) {
		this.message = message;
		this.resultCode = resultCode;
		this.data = data;
		this.code = resultCode.getCode();
	}

	public static WrapperResult success() {
		WrapperResult result = new WrapperResult();
		result.setResultCode(ResultCodeEnum.SUCCESS);
		result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(Collections.EMPTY_MAP);
		return result;
	}

	public static WrapperResult success(Object data) {
		WrapperResult result = new WrapperResult();
		result.setResultCode(ResultCodeEnum.SUCCESS);
		result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		result.setData(data);
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		return result;
	}

	public static WrapperResult success(String message, Object data) {
		return new WrapperResult(message, ResultCodeEnum.SUCCESS, data);
	}

	public static WrapperResult fail(ResultCodeEnum code) {
		if (code == null) {
			code = ResultCodeEnum.SYS_ERR;
		}
		return new WrapperResult(code.getMsg(), code, Collections.EMPTY_MAP);
	}

	public static WrapperResult fail(ResultCodeEnum code, String msg) {
		if (msg == null) {
			return WrapperResult.fail(code);
		}
		if (code == null) {
			code = ResultCodeEnum.SYS_ERR;
		}
		return new WrapperResult(msg, code, Collections.EMPTY_MAP);
	}

	@JsonIgnore
	public boolean isSuccess() {
		return ResultCodeEnum.SUCCESS.getCode().equals(this.getCode());
	}

	@JsonIgnore
	public ResultCodeEnum getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCodeEnum resultCode) {
		this.resultCode = resultCode;
		this.code = resultCode.getCode();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("code", this.getCode());
		json.put("message", this.getMessage());
		json.put("data", this.getData());
		return json.toJSONString();
	}
}
