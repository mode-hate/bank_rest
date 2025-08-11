package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;


public class CardSpecifications {

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> hasOwner(User owner) {
        return (root, query, cb) ->
                owner == null ? cb.conjunction() : cb.equal(root.get("owner"), owner);
    }

    public static Specification<Card> fetchOwner() {
        return (root, query, cb) -> {

            if (Card.class.equals(query.getResultType())) {
                root.fetch("owner", JoinType.LEFT);

                query.distinct(true);
            }
            return null;
        };
    }
}
