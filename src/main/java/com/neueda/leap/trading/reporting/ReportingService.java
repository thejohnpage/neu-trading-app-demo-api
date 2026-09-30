package com.neueda.leap.trading.reporting;
import java.util.*; import org.springframework.stereotype.Service;
/** Provides admin reports from the warehouse through MyBatis. */
@Service public class ReportingService {
 private final WarehouseReportingMapper reports; public ReportingService(WarehouseReportingMapper reports){this.reports=reports;}
 public Map<String,Object> summary(){return reports.summary();} public List<Map<String,Object>> activity(){return reports.activity();} public List<Map<String,Object>> instruments(){return reports.instruments();} public List<Map<String,Object>> clientSegments(){return reports.clientSegments();} public List<Map<String,Object>> volume(){return reports.volume();}
}