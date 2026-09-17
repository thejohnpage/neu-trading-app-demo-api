package com.neueda.leap.trading.reporting;
import java.util.List; import java.util.Map; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/reports")
public class ReportingController {
 private final ReportingService reports; public ReportingController(ReportingService reports){this.reports=reports;}
 @GetMapping("/summary") public Map<String,Object> summary(){return reports.summary();}
 @GetMapping("/activity") public List<Map<String,Object>> activity(){return reports.activity();}
 @GetMapping("/instruments") public List<Map<String,Object>> instruments(){return reports.instruments();}
 @GetMapping("/client-segments") public List<Map<String,Object>> clientSegments(){return reports.clientSegments();}
 @GetMapping("/volume") public List<Map<String,Object>> volume(){return reports.volume();}
}
