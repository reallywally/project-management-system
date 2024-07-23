package com.wally.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wally.request.ProjectSearch;
import com.wally.response.ProjectResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

import static com.wally.model.QProject.project;
import static com.wally.model.QProjectUser.projectUser;

@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ProjectResp> getList(ProjectSearch projectSearch) {

        long totalCount = jpaQueryFactory.select(project.count())
                .from(project)
                .fetchFirst();

        List<ProjectResp> projectResps = jpaQueryFactory
                .select(Projections.constructor(ProjectResp.class, project))
                .from(project)
                    .leftJoin(projectUser).on(project.id.eq(projectUser.projectId))
                .limit(projectSearch.getSize())
                .offset(projectSearch.getOffset())
                .where(buildWhereExpressions(projectSearch))
                .fetch();


        return new PageImpl<>(projectResps, projectSearch.getPageable(), totalCount);
    }

    private Predicate[] buildWhereExpressions(ProjectSearch projectSearch) {
        List<Predicate> expressions = new ArrayList<>();

        if (projectSearch.getName() != null) {
            expressions.add(project.name.contains(projectSearch.getName()));
        }

        if (projectSearch.getCategory() != null) {
            expressions.add(project.category.eq(projectSearch.getCategory()));
        }

//        if(projectSearc.getTags() != null && !projectSearch.getTags().isEmpty()) {
//            expressions.add(project.tags.any().in(projectSearch.getTags()));
//        }

        if (projectSearch.getOwnerId() != null) {
            expressions.add(project.ownerId.eq(projectSearch.getOwnerId()));
        }

        return expressions.toArray(Predicate[]::new);
    }
}
