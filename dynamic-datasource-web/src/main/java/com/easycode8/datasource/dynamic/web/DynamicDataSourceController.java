package com.easycode8.datasource.dynamic.web;


import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.core.util.SpringExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/")
public class DynamicDataSourceController {

    @Autowired
    private DynamicDataSourceManager dynamicDataSourceManager;

    @GetMapping ("/dynamic-datasource.html")
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        String apiPrefix = "/";
        model.addAttribute("apiUrl", apiPrefix);
        Map<String, Object> front = new HashMap<>();

        front.put("iview_css", apiPrefix + "webjars/dynamic-datasource-ui/iview/style/iview.css");
        front.put("iview_js", apiPrefix + "webjars/dynamic-datasource-ui/iview/iview.min.js");
        front.put("vue_js", apiPrefix + "webjars/dynamic-datasource-ui/vue/vue.js");
        front.put("axios_js", apiPrefix + "webjars/dynamic-datasource-ui/axios/axios.min.js");
        model.addAttribute("front", front);
        return "dynamic-datasource";
    }

    @GetMapping("/dynamic-datasource-ui/list")
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", dynamicDataSourceManager.listAllDataSourceInfo());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/dynamic-datasource-ui/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody DataSourceInfo info) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "新增数据源成功");
        try {
            dynamicDataSourceManager.addDynamicDataSource(info);
        } catch (Throwable e) {
            SpringExceptionUtils.handleRootException(e, (ex) -> {
                result.put("message", "新增数据源失败:" + e.getMessage());
                result.put("success", false);
            });
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/dynamic-datasource-ui/remove")
    public ResponseEntity<Map<String, Object>> remove(DataSourceInfo info) {
        Map<String, Object> result = new HashMap<>();
        SpringExceptionUtils.runIgnoreException(() -> {
                    dynamicDataSourceManager.removeDynamicDataSource(info.getKey());
                    return new HashMap<>();
                },
                (e) -> {
                    result.put("message", "移除数据源失败:" + e.getMessage());
                    result.put("success", false);
                });
        return ResponseEntity.ok(result);
    }

}
