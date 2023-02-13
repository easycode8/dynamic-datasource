package com.easycode8.datasource.dynamic.autoconfigure;

import com.easycode8.datasource.dynamic.core.DynamicDataSourceManager;
import com.easycode8.datasource.dynamic.web.configuration.DynamicDataSourceWebConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureAfter({DynamicDataSourceAutoConfiguration.class})
@ConditionalOnBean(DynamicDataSourceManager.class)
@Import(DynamicDataSourceWebConfiguration.class)
public class DynamicDataSourceWebAutoConfiguration {
}
