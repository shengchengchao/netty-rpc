package com.xixi.netty.rpc.server;

import lombok.Data;

import java.util.List;

/**
 * @author shengchengchao
 * @Description
 * @createTime 2021/10/4
 */
@Data
public class MethodMatchInput {

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数签名
     */
    private List<String> methodArgumentSignatures;

    /**
     * 方法参数长度
     */
    private Integer methodArgsLength;

}
