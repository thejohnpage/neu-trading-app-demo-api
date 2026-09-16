package com.neueda.leap.trading.position;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, PositionId> {

    @EntityGraph(attributePaths = "instrument")
    List<Position> findByAccountIdInOrderByAccountIdAscInstrumentIdAsc(Collection<UUID> accountIds);
}
