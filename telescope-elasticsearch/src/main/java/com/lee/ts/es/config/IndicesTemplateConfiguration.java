package com.lee.ts.es.config;

import com.lee.ts.es.component.template.IndicesElasticsearchTemplate;
import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

/**
 * 全量索引elasticsearch template初始化配置类
 * AutoConfigureAfter 只有把Configuration修饰的类，维护到spring.factories文件中才会生效，
 * 不然只要spring scan到就会立刻加载，导致无法依赖到对应的bean
 * @author liwei
 * @date 2019-08-07 17:43
 */
@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class})
@AutoConfigureAfter(ElasticsearchDataAutoConfiguration.class)
public class IndicesTemplateConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ElasticsearchConverter.class, ElasticsearchTemplate.class})
    public IndicesElasticsearchTemplate indicesElasticsearchTemplate(ElasticsearchConverter converter,
                                                                     ElasticsearchTemplate elasticsearchTemplate) {
        return new IndicesElasticsearchTemplate(elasticsearchTemplate.getClient(),
                converter, elasticsearchTemplate);
    }
}
