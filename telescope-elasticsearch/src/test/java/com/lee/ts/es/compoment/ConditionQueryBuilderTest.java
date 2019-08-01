package com.lee.ts.es.compoment;

import com.lee.ts.es.condition.EsSearchCondition;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author liwei
 * @date 2019-08-01 19:28
 */
public class ConditionQueryBuilderTest {

    @Test
    public void testBuilder() {
        EsSearchCondition condition = new EsSearchCondition();
        condition.setDatabase("wms");
        condition.setTable("auth_user");
        condition.setType("insert");
        condition.setHappenTimeStart(new Date());
        condition.setHappenTimeEnd(new Date());
        condition.setXid("123");
        condition.setDataKeyword("asdfsad");
        QueryBuilder builder = new ConditionQueryBuilder().builder(condition);
        Assert.notNull(builder, "创建builder失败");
    }
}