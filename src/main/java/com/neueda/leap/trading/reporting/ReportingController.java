package com.neueda.leap.trading.reporting;

import java.util.List; import java.util.Map;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Admin Reporting",description="Warehouse-backed business and trading reports")
@RestController @RequestMapping("/api/v1/admin/reports")
public class ReportingController {
 private final ReportingService reports; public ReportingController(ReportingService reports){this.reports=reports;}
 @Operation(summary="Get reporting summary",description="Returns high-level trading and warehouse metrics.") @GetMapping("/summary") public Map<String,Object> summary(){return reports.summary();}
 @Operation(summary="Get trading activity",description="Returns recent completed trade activity from the reporting read model.") @GetMapping("/activity") public List<Map<String,Object>> activity(){return reports.activity();}
 @Operation(summary="Get instrument report",description="Aggregates trading activity by instrument.") @GetMapping("/instruments") public List<Map<String,Object>> instruments(){return reports.instruments();}
 @Operation(summary="Get client segment report",description="Aggregates trading activity by client segment.") @GetMapping("/client-segments") public List<Map<String,Object>> clientSegments(){return reports.clientSegments();}
 @Operation(summary="Get trading volume",description="Returns trading volume aggregated by date.") @GetMapping("/volume") public List<Map<String,Object>> volume(){return reports.volume();}
}
