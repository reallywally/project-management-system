package com.wally.repository;

import com.wally.request.ProjectSearch;
import com.wally.response.ProjectResp;
import org.springframework.data.domain.Page;

public interface ProjectRepositoryCustom {
    Page<ProjectResp> getList(ProjectSearch projectSearch);
}
