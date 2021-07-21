package org.tdf.sim.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tdf.sim.type.Response;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.FormatUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class HardwareController {

    // 获取服务器硬件信息
    @GetMapping("/hardware")
    public Response<Map<String, Object>> hardware() throws InterruptedException {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        Map<String, Object> map = new HashMap<>();
        map.put("memory_avail", FormatUtil.formatBytes(hal.getMemory().getAvailable()));
        map.put("memory_total", FormatUtil.formatBytes(hal.getMemory().getTotal()));
        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        map.put("memory_utilization", new DecimalFormat("#.##%").format((hal.getMemory().getTotal() - hal.getMemory().getAvailable()) * 1.0 / hal.getMemory().getTotal()));
        map.put("cpu_cores", processor.getLogicalProcessorCount());
        map.put("cpu_sys_utilization", new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu)); // cpu系统使用率
        map.put("cpu_user_utilization", new DecimalFormat("#.##%").format(user * 1.0 / totalCpu)); // cpu用户使用率
        map.put("cpu_user_current_waiting_utilization", new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu)); // cpu当前等待率
        map.put("cpu_user_current_utilization", new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu))); // cpu当前使用率
        return Response.success(map);
    }
}
