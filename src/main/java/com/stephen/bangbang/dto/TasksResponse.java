package com.stephen.bangbang.dto;

import java.util.List;

public class TasksResponse extends PaginationResponse {

    List<TaskSnapshot> snapshots;

    public TasksResponse(Pagination pagination, List<TaskSnapshot> snapshots) {
        super(pagination);
        this.snapshots = snapshots;
    }

    public List<TaskSnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<TaskSnapshot> snapshots) {
        this.snapshots = snapshots;
    }
}
