package com.stephen.bangbang.dto;

// 用来分页
public class PaginationResponse extends BaseResponse {
    Pagination pagination;

    public PaginationResponse(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
