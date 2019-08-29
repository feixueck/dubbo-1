/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.protocol.rest;

import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

//
public class RpcExceptionMapper implements ExceptionMapper<RpcException> {

    @Override
    public Response toResponse(RpcException e) {
        // TODO do more sophisticated exception handling and output 做更复杂的异常处理和输出
        if (e.getCause() instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) e.getCause());
        }
        // we may want to avoid exposing the dubbo exception details to certain clients 我们可能希望避免向某些客户端公开dubbo异常细节
        // TODO for now just do plain text output 现在只做纯文本输出
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error: " + e.getMessage()).type(ContentType.TEXT_PLAIN_UTF_8).build();
    }

    protected Response handleConstraintViolationException(ConstraintViolationException cve) {
        ViolationReport report = new ViolationReport();
        for (ConstraintViolation cv : cve.getConstraintViolations()) {
            report.addConstraintViolation(new RestConstraintViolation(
                    cv.getPropertyPath().toString(),
                    cv.getMessage(),
                    cv.getInvalidValue() == null ? "null" : cv.getInvalidValue().toString()));
        }
        // TODO for now just do xml output 现在只执行xml输出
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(report).type(ContentType.TEXT_XML_UTF_8).build();
    }
}
