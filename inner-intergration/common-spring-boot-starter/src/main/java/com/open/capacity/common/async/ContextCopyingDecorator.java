package com.open.capacity.common.async;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

// https://stackoverflow.com/questions/23732089/how-to-enable-request-scope-in-async-task-executor
// 传递RequestAttributes and MDC
public class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        try {
			RequestAttributes context = RequestContextHolder.currentRequestAttributes(); 
			Map<String,String> previous = MDC.getCopyOfContextMap(); 
			return () -> {
			    try {
			    	if(previous==null){
			    		MDC.clear();
			    	}else{
			    		MDC.setContextMap(previous);
			    	}
			    	
			        RequestContextHolder.setRequestAttributes(context);
			        runnable.run();
			    } finally {
			        RequestContextHolder.resetRequestAttributes();
			        if(previous==null){
			    		MDC.clear();
			    	}else{
			    		MDC.setContextMap(previous);
			    	}
			    }
			};
		} catch (IllegalStateException e) {
			return runnable;
		}
    }
}
