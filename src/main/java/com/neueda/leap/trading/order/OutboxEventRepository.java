package com.neueda.leap.trading.order;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for the transactional outbox. */
@Mapper public interface OutboxEventRepository {
 @Insert("INSERT INTO trading.outbox_events(event_id,aggregate_type,aggregate_id,event_type,payload,created_at,published_at) VALUES(#{eventId},#{aggregateType},#{aggregateId},#{eventType},CAST(#{payload} AS jsonb),#{createdAt},#{publishedAt})") int insert(OutboxEvent e);
 @Select("SELECT event_id eventId,aggregate_type aggregateType,aggregate_id aggregateId,event_type eventType,payload::text payload,created_at createdAt,published_at publishedAt FROM trading.outbox_events WHERE published_at IS NULL ORDER BY created_at LIMIT 50") List<OutboxEvent> findUnpublished();
 @Update("UPDATE trading.outbox_events SET published_at=#{publishedAt} WHERE event_id=#{eventId} AND published_at IS NULL") int markPublished(OutboxEvent e);
}
