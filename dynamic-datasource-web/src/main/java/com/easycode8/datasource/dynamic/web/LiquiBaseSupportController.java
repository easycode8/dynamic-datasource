package com.easycode8.datasource.dynamic.web;



import com.easycode8.datasource.dynamic.core.DataSourceInfo;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceHolder;
import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/dynamic-datasource-ui/liquibase")
public class LiquiBaseSupportController {


    @Autowired(required = false)
    private DynamicDataSourceManager dynamicDataSourceManager;

    @Autowired
    private SpringLiquibase springLiquibase;



    @PostMapping("initAll")
    public ResponseEntity<String> runLiquibaseAll(@RequestParam(defaultValue = "false") boolean dropFirst) throws LiquibaseException {
        if (dynamicDataSourceManager == null) {
            return ResponseEntity.ok("动态数据源未开启");
        }
        for (DataSourceInfo dataSourceInfo : this.dynamicDataSourceManager.listAllDataSourceInfo()) {
            DynamicDataSourceHolder.push(dataSourceInfo.getKey());
            springLiquibase.setDropFirst(dropFirst);
            springLiquibase.afterPropertiesSet();
            springLiquibase.setDropFirst(false);
            DynamicDataSourceHolder.poll();
        }
        return ResponseEntity.ok("手动执行liquibase成功");

    }
}
