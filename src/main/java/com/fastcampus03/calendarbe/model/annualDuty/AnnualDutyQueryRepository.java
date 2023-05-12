package com.fastcampus03.calendarbe.model.annualDuty;

import com.fastcampus03.calendarbe.model.log.update.UpdateRequestLog;
import com.fastcampus03.calendarbe.model.user.QUser;
import com.fastcampus03.calendarbe.model.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.fastcampus03.calendarbe.model.annualDuty.QAnnualDuty.*;

@Repository
public class AnnualDutyQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    private final int SIZE = 8;

    public AnnualDutyQueryRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public Page<AnnualDuty> findAllByStatus(Integer page, String approving) {
        int startPosition = page * SIZE;
//
//        String jpql = "select ad from AnnualDuty ad join fetch ad.user where ad.status=:status";
//
//        List<AnnualDuty> annualDutyListPS = em.createQuery(jpql)
//                .setParameter("status", approving)
//                .setFirstResult(startPosition) // 시작 번호
//                .setMaxResults(SIZE) // 개수
//                .getResultList();
//
//        Long totalCount = em.createQuery("select count(ad) from AnnualDuty ad", Long.class).getSingleResult();
//        return new PageImpl<>(annualDutyListPS, PageRequest.of(page, SIZE), totalCount);

        List<AnnualDuty> annualDutyListPS = query
                .selectFrom(annualDuty)
                .join(annualDuty.user, QUser.user)
                .fetchJoin()
                .where(annualDuty.status.eq(approving))
                .orderBy(annualDuty.id.desc())
                .offset(startPosition)
                .limit(SIZE)
                .fetch();

        Long totalCount = query
                .selectFrom(annualDuty)
                .fetchCount();

        return new PageImpl<>(annualDutyListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<AnnualDuty> findAllByUpdateStatus(Integer page, int updateStatus) {
        int startPosition = page * SIZE;

        String jpql = "select ad from AnnualDuty ad join fetch ad.user where ad.updateStatus=:updateStatus";

        List<AnnualDuty> annualDutyListPS = em.createQuery(jpql)
                .setParameter("updateStatus", updateStatus)
                .setFirstResult(startPosition) // 시작 번호
                .setMaxResults(SIZE) // 개수
                .getResultList();

        Long totalCount = em.createQuery("select count(ad) from AnnualDuty ad", Long.class).getSingleResult();
        return new PageImpl<>(annualDutyListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<UpdateRequestLog> findAllByUpdateLogStatus(Integer page, boolean status) {
        int startPosition = page * SIZE;

        String jpql = "select ur from UpdateRequestLog ur join fetch ur.annualDuty where ur.status=:status";

        List<UpdateRequestLog> annualDutyListPS = em.createQuery(jpql)
                .setParameter("status", status)
                .setFirstResult(startPosition) // 시작 번호
                .setMaxResults(SIZE) // 개수
                .getResultList();

        Long totalCount = em.createQuery("select count(ur) from UpdateRequestLog ur", Long.class).getSingleResult();
        return new PageImpl<>(annualDutyListPS, PageRequest.of(page, SIZE), totalCount);
    }

    public Page<User> findAllUser(Integer page) {
        int startPosition = page * SIZE;

        String jpql = "select u from User u";

        List<User> usersList = em.createQuery(jpql)
                .setFirstResult(startPosition) // 시작 번호
                .setMaxResults(SIZE) // 개수
                .getResultList();

        Long totalCount = em.createQuery("select count(u) from User u", Long.class).getSingleResult();
        return new PageImpl<>(usersList, PageRequest.of(page, SIZE), totalCount);
    }
}