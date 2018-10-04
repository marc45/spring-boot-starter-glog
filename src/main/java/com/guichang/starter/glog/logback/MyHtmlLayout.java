
package com.guichang.starter.glog.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.pattern.Converter;

import java.util.Map;

import static ch.qos.logback.core.CoreConstants.LINE_SEPARATOR;

/**
 * 修改源码，主要是将Message和异常栈打在一起
 * @author guichang
 * @author 2017/11/18
 */
public class MyHtmlLayout extends HTMLLayoutBase<ILoggingEvent> {

    /**日志标识*/
    private static final String LOG_MESSAGE = "Message";

    /**
     * Default pattern string for log output.
     */
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";

    MyThrowableRenderer throwableRenderer;

    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     *
     * The default pattern just produces the application supplied message.
     */
    public MyHtmlLayout() {
        pattern = DEFAULT_CONVERSION_PATTERN;
        throwableRenderer = new MyThrowableRenderer();
        cssBuilder = new MyCssBuilder();
    }

    @Override
    public void start() {
        int errorCount = 0;
        if (throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            errorCount++;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    @Override
    protected Map<String, String> getDefaultConverterMap() {
        return PatternLayout.defaultConverterMap;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();
        startNewTableIfLimitReached(buf);

        boolean odd = true;
        if (((counter++) & 1) == 0) {
            odd = false;
        }

        String level = event.getLevel().toString().toLowerCase();

        buf.append(LINE_SEPARATOR);
        buf.append("<tr class=\"");
        buf.append(level);
        if (odd) {
            buf.append(" odd\">");
        } else {
            buf.append(" even\">");
        }
        buf.append(LINE_SEPARATOR);

        Converter<ILoggingEvent> c = head;
        while (c != null) {

            if(LOG_MESSAGE.equals(computeConverterName(c))) {
                // 解析后的message
                throwableRenderer.setMessage(Transform.escapeTags(c.convert(event)));
            }

            appendEventToBuffer(buf, c, event);
            c = c.getNext();
        }
        buf.append("</tr>");
        buf.append(LINE_SEPARATOR);

        if (event.getThrowableProxy() != null) {
            throwableRenderer.render(buf, event);
        }
        return buf.toString();
    }

    private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
        String str = computeConverterName(c);
        String content = Transform.escapeTags(c.convert(event));
        // 若有异常则不将Message放入table显示 guichang
        if (event.getThrowableProxy() != null && LOG_MESSAGE.equals(str)) {
            content = "　　　　　";
        }

        buf.append("<td class=\"");
        buf.append(str);
        buf.append("\">");
        buf.append(content);
        buf.append("</td>");
        buf.append(LINE_SEPARATOR);
    }

    @Override
    protected String computeConverterName(Converter c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter) c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            } else {
                return "MDC";
            }
        } else {
            return super.computeConverterName(c);
        }
    }

}
