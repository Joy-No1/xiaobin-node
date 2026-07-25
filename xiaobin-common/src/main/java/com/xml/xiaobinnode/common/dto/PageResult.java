package com.xml.xiaobinnode.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页码 */
    private long page;

    /** 每页大小 */
    private long size;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 数据列表 */
    private List<T> records;

    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        long pages = total % size == 0 ? total / size : total / size + 1;
        return new PageResult<>(page, size, total, pages, records);
    }
}
