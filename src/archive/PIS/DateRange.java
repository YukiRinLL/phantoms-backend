package com.phantoms.phantomsbackend.common.utils.PIS;

import java.io.Serializable;
import java.util.Date;

/**
 * @version V1.0
 * @Package com.kingyun.gpsinspection.pipelineservice.common.constant
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @author: lutianbo
 * @date: 2017/10/17,0017
 */

public class DateRange implements Serializable
{
    private Date start;
    private Date end;

    public DateRange(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }
    public void setStart(Date start) {
        this.start = start;
    }
    public Date getEnd() {
        return end;
    }
    public void setEnd(Date end) {
        this.end = end;
    }
}
