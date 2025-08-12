package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {


    @Query("""
            select t.id
            from Transfer t
            where t.fromCard.id = :cardId
            or t.toCard.id = :cardId
            """)
    Page<Long> findTransferIdsByCardId(@Param("cardId") Long cardId, Pageable pageable);



    @Query("""
            select distinct t from Transfer t
            left join fetch t.fromCard
            left join fetch t.toCard
            join fetch t.owner
            where t.id in :ids
            """)
    List<Transfer> findAllByIds(@Param("ids") List<Long> ids);
}
