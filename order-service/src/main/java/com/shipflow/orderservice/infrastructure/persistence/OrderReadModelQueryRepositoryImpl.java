package com.shipflow.orderservice.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shipflow.orderservice.application.dto.OrderSearchCondition;
import com.shipflow.orderservice.domain.model.OrderReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderReadModelQueryRepositoryImpl implements OrderReadModelQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<OrderReadModel> search(OrderSearchCondition condition, Pageable pageable) {
        QOrderReadModelJpaEntity q = QOrderReadModelJpaEntity.orderReadModelJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();

        // 소프트 딜리트 제외
        builder.and(q.deletedAt.isNull());

        // 검색 조건 (null이면 추가 안 함)
        if (condition.status() != null) {
            builder.and(q.orderStatus.eq(condition.status()));
        }
        if (condition.ordererId() != null) {
            builder.and(q.ordererId.eq(condition.ordererId()));
        }
        if (condition.productId() != null) {
            builder.and(q.productId.eq(condition.productId()));
        }
        if (condition.supplierCompanyId() != null) {
            builder.and(q.supplierCompanyId.eq(condition.supplierCompanyId()));
        }
        if (condition.receiverCompanyId() != null) {
            builder.and(q.receiverCompanyId.eq(condition.receiverCompanyId()));
        }
        if (condition.createdFrom() != null) {
            builder.and(q.createdAt.goe(condition.createdFrom()));
        }
        if (condition.createdTo() != null) {
            builder.and(q.createdAt.loe(condition.createdTo()));
        }

        OrderSpecifier<?>[] orderSpecifiers = toOrderSpecifiers(pageable.getSort(), q);

        // Slice 패턴: pageSize+1개 조회 후 hasNext 판단
        List<OrderReadModelJpaEntity> rows = queryFactory
                .selectFrom(q)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = rows.size() > pageable.getPageSize();
        if (hasNext) {
            rows = rows.subList(0, pageable.getPageSize());
        }

        List<OrderReadModel> content = rows.stream()
                .map(OrderReadModelJpaEntity::toDomain)
                .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QOrderReadModelJpaEntity q) {
        List<OrderSpecifier<?>> specifiers = new ArrayList<>();
        for (Sort.Order order : sort) {
            boolean asc = order.isAscending();
            OrderSpecifier<?> spec = switch (order.getProperty()) {
                case "createdAt" -> asc ? q.createdAt.asc() : q.createdAt.desc();
                case "updatedAt" -> asc ? q.updatedAt.asc() : q.updatedAt.desc();
                default          -> q.createdAt.desc();
            };
            specifiers.add(spec);
        }
        if (specifiers.isEmpty()) {
            specifiers.add(q.createdAt.desc());
        }
        return specifiers.toArray(new OrderSpecifier[0]);
    }
}
