
package com.guichang.starter.glog.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.IThrowableRenderer;
import lombok.Data;

/**
 * 修改源码，主要是将Message和异常栈打在一起
 * @author guichang
 * @author 2017/11/18
 */
@Data
public class MyThrowableRenderer implements IThrowableRenderer<ILoggingEvent> {
    /**
     * Message
     */
    private String message;

    static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";

    @Override
    public void render(StringBuilder sbuf, ILoggingEvent event) {
        IThrowableProxy tp = event.getThrowableProxy();
        sbuf.append("<tr><td class=\"Exception\" colspan=\"10\">");

        // 此处将Message写在异常栈上面 桂畅
        sbuf.append(message + "<br/>");

        while (tp != null) {
            render(sbuf, tp);
            tp = tp.getCause();
        }
        sbuf.append("</td></tr>");
    }

    void render(StringBuilder sbuf, IThrowableProxy tp) {
        printFirstLine(sbuf, tp);

        int commonFrames = tp.getCommonFrames();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();

        for (int i = 0; i < stepArray.length - commonFrames; i++) {
            StackTraceElementProxy step = stepArray[i];
            sbuf.append(TRACE_PREFIX);
            sbuf.append(Transform.escapeTags(step.toString()));
            sbuf.append(CoreConstants.LINE_SEPARATOR);
        }

        if (commonFrames > 0) {
            sbuf.append(TRACE_PREFIX);
            sbuf.append("\t... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public void printFirstLine(StringBuilder sb, IThrowableProxy tp) {
        int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            sb.append("<br />").append(CoreConstants.CAUSED_BY);
        }
        sb.append(tp.getClassName()).append(": ").append(Transform.escapeTags(tp.getMessage()));
        sb.append(CoreConstants.LINE_SEPARATOR);
    }

}
