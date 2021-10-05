package com.xixi.netty.rpc.client;

import lombok.Data;

import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/5
 */
@Data
public class RequestArgumentOutput {

    private String interfaceName;

    private String methodName;

    private List<String> methodArgumentsType;
}
